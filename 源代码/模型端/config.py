# config.py
import os

class Config:
	WENXIN_API_URL = "https://qianfan.baidubce.com/v2/chat/completions"
	
	# 这里示例写死，但更安全的做法是用环境变量：
	WENXIN_API_KEY = "API KEY"
	
	REDIS_URL = "redis://localhost:6379/0"

	REDIS_EXPIRE_SECONDS = 1800  # 默认过期时间
