# 需要记录用户上下文
# services/session_store.py

import uuid
import json
import redis
# from config import Config
from AICameraBackend.config import Config
from redis import ConnectionPool
# 你可以在 config.py 里定义 Config.REDIS_URL = "redis://localhost:6379/0"
#decode_responses=True，让 redis 返回字符串而非字节。每个会话的消息列表用 json.dumps() 存储。

# 创建连接池（全局唯一）
redis_pool = ConnectionPool.from_url(
    Config.REDIS_URL,
    decode_responses=True,  # 自动解码返回字符串
    max_connections=20      # 根据负载调整
)

# r = redis.from_url(Config.REDIS_URL, decode_responses=True)

class RedisSessionStore:
	"""
	用 Redis 来存储会话记录
	"""
	# def __init__(self, redis_client):
	def __init__(self):

		# self.redis_client = redis_client
		self.redis_client = redis.Redis(connection_pool=redis_pool)

	def create_session_id(self):
		"""
		生成随机 session_id
		"""
		return str(uuid.uuid4())

	def get_messages(self, session_id):
		"""
		从 Redis 获取对话消息列表
		"""
		data = self.redis_client.get(session_id)
		if not data:
			return []
		try:
			return json.loads(data)  # 反序列化成 Python list
		except json.JSONDecodeError:
			return []

	# def save_messages(self, session_id, messages):
	#     """
	#     将对话消息列表（list）序列化后存到 Redis
	#     """
	#     data = json.dumps(messages, ensure_ascii=False)
	#     self.redis_client.set(session_id, data)

	# def save_messages(self, session_id, messages):
	# 	# 类型检查
	# 	if not isinstance(messages, list):
	# 		raise TypeError("messages 必须是列表")
		
	# 	# 数据结构校验
	# 	for msg in messages:
	# 		if not isinstance(msg, dict) or "role" not in msg or "content" not in msg:
	# 			raise ValueError("消息项缺少 role/content 字段")
		
	# 	# 序列化 + 编码
	# 	try:
	# 		data = json.dumps(messages, ensure_ascii=False)
	# 	except TypeError as e:
	# 		raise ValueError(f"无法序列化消息: {str(e)}")
		
	# 	# 强制使用 bytes 存储
	# 	self.redis_client.set(session_id, data.encode('utf-8'))
		
	#存的是JSON 字符串	
	def save_messages(self, session_id, messages, expire_seconds=1800):
		"""
		将对话消息列表（list）序列化后存到 Redis
		"""
		# 1. 类型检查（可选）
		if not isinstance(messages, list):
			raise TypeError("messages 必须是列表")
		
		# 2. 数据结构校验（可选，看你实际需要）
		for msg in messages:
			if not isinstance(msg, dict) or "role" not in msg or "content" not in msg:
				raise ValueError("消息项缺少 role/content 字段")
		
		# 3. 使用 JSON 序列化
		data_str = json.dumps(messages, ensure_ascii=False)
		
		# 4. 存入 Redis
		# # 如果想直接以字符串形式存，可以这样：
		# self.redis_client.set(session_id, data_str)
		 # 存入 Redis 时添加过期时间
		self.redis_client.setex(session_id, expire_seconds, data_str)  # 使用 setex 替代 set
		# 如果想以 bytes 存储，可以：
		# self.redis_client.set(session_id, data_str.encode('utf-8'))

			
	def session_exists(self, session_id):
		"""
		判断 Redis 里是否有该 key
		"""
		if(type(session_id)==list) :
			return self.redis_client.exists(session_id[0]) == 1
		return self.redis_client.exists(session_id) == 1


# 实例化一个全局 store
SessionStore = RedisSessionStore()
