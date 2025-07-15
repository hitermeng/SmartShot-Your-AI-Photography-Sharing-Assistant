# routes/caption.py
from flask import Blueprint, request, jsonify
from AICameraBackend.services.ai_service import get_caption, get_caption_with_context
import os
from AICameraBackend.services.session_store import SessionStore
from flask import current_app
import json
from AICameraBackend.config import Config

caption_bp = Blueprint('caption_bp', __name__)

@caption_bp.route('/generate', methods=['POST'])
def generate_caption():
	try:
		"""
		开启多轮对话的第一步：用户上传图片+初次指令s
		- 生成 session_id
		- 调用大模型得到初版文案
		- 存储对话上下文(到Redis)
		"""
		platform = request.form.get('platform')
		print(platform)
		user_mood = request.form.get('mood', '')
		print(user_mood)

		if not platform:
			return jsonify({"error": "platform is required"}), 400

		uploaded_files = request.form.getlist('images')

		# print("len")
		# print(len(uploaded_files))
		
		if not uploaded_files:
			return jsonify({"error": "No photo file provided"}), 400


		try:
			# 直接将图片列表传给 get_caption，无论只有一张还是多张
			first_caption , message_content= get_caption(uploaded_files, platform, user_mood)
		except Exception as e:
			return jsonify({"error": str(e)}), 500
		
		# 处理API错误响应
		if "error" in first_caption:
			return jsonify({"error": first_caption["error"]}), 500

		try:
			# 从返回结果中提取大模型返回的文本，并只取“文案内容：”后面的部分
			full_content = first_caption["choices"][0]["message"]["content"]
			parts = full_content.split("文案内容：", 1)
			if len(parts) == 2:
				caption_text = parts[1].strip()
			else:
				caption_text = full_content.strip()
		except Exception as e:
			return jsonify({"error": "Failed to extract caption", "detail": str(e)}), 500
		
		# 新增：创建 session_id 并初始化对话历史
		sessionId = SessionStore.create_session_id()
		# messages = []#messages 是一个 Python 列表
		# messages.append({"role": "user", "content": message_content})
		# messages.append({"role": "assistant", "content": caption_text})
		messages = [
    {
        "role": "user",
        "content": message_content  # 保持原始结构 [{"type":text}, {"type":image_url}]
    },
    {
        "role": "assistant",
        "content": [{"type": "text", "text": caption_text}]  # 统一为列表格式
    }
]
		SessionStore.save_messages(sessionId, messages, Config.REDIS_EXPIRE_SECONDS)

		return jsonify({
		"sessionId": sessionId,
		"caption": caption_text
	})
	except Exception as e:
		import traceback
		# 打印错误堆栈（关键）
		current_app.logger.error("生成文案时发生错误: %s", str(e))
		current_app.logger.error(traceback.format_exc())  # 打印完整堆栈
		return jsonify({"error": str(e)}), 500
	# # 返回结果仅包含提取后的文案字符串（不再包含键名）
	# response = current_app.response_class(
	# 	response=json.dumps(caption_text),
	# 	status=200,
	# 	mimetype='application/json'
	# )
	# return response
	# response_data = {
	# "session_id": sessionId,
	# "caption": caption_text
	# }
	# response = current_app.response_class(
	# 	response=json.dumps(response_data),
	# 	status=200,
	# 	mimetype='application/json'
	# )
	# return response


@caption_bp.route('/conversation_update', methods=['POST'])
def update_conversation():
	"""
	多轮对话: 用户基于已有 session_id, 提新的修改要求, 让模型改写文案
	前端传:
	  - session_id
	  - new_request
	返回:
	  - new_caption
	"""
	data = request.json or request.form
	session_id = data.get("sessionId")[0]
	new_request = data.get("message", "")[0]

	# session_id = data.get("sessionId")  # 移除 [0]
	# new_request = data.get("message", "")  # 移除 [0]

	print(session_id)
	print(new_request)
	# 添加空值校验
	if not isinstance(session_id, str) or len(session_id) != 36:
		return jsonify({"error": "Invalid sessionId format"}), 400

	if not session_id or not SessionStore.session_exists(session_id):
		return jsonify({"error": "Invalid or missing session_id"}), 400

	if not new_request.strip():
		return jsonify({"error": "No new_request content provided"}), 400

	# 取出已有对话消息
	messages = SessionStore.get_messages(session_id)

	# # 加上用户的新请求
	# messages.append({"role": "user", "content": new_request})

	 # 正确添加用户新请求（保持官方 API 结构）
	messages.append({
		"role": "user",
		"content": [{"type": "text", "text": new_request}]
	})

	# 调用大模型进行改写
	try:
		new_caption = get_caption_with_context(messages)
	except Exception as e:
		return jsonify({"error": str(e)}), 500

	# # 把模型新的回复也加入到对话
	# messages.append({"role": "assistant", "content": new_caption})

	 # 把模型新的回复也加入到对话（格式化为 API 结构）
	messages.append({
		"role": "assistant",
		"content": [{"type": "text", "text": new_caption}]
	})

	# 保存回 Redis
	SessionStore.save_messages(session_id, messages, Config.REDIS_EXPIRE_SECONDS)

	# return jsonify({
	# 	# "sessionId": session_id,
	# 	"caption": new_caption
	# }), 200

	return new_caption,200
