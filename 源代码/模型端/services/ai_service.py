# services/ai_service.py
import requests
import json
from AICameraBackend.config import Config
import base64
import uuid
import redis

# import traceback
from AICameraBackend.services.session_store import SessionStore
# from prompts import PLATFORM_PROMPTS

def call_wenxin_api(text_prompt, image_url):
	"""
	调用 DeepSeek-VL2多模态模型, 返回美学评分和移动建议等.
	你可以根据实际需求修改 task / payload 格式。
	"""
	# url = "https://qianfan.baidubce.com/v2/chat/completions"
	url = Config.WENXIN_API_URL
	headers = {
		"Content-Type": "application/json",
		"Authorization": f"Bearer {Config.WENXIN_API_KEY}",
		# "Authorization": "Bearer bce-v3/ALTAK-EV929lNomcyUDXusAKPiy/3671557eef6ae3529b434e7752edef258c68b202",
	}

	# 根据官方文档，需要的请求体
	payload = {
		"model": "deepseek-vl2",  # 模型名
		"messages": [
			{
				"role": "user",
				"content": [
					{
						"type": "text",
						"text": text_prompt
					},
					{
						"type": "image_url",
						"image_url": {
							"url": image_url
						}
					}
				]
			}
		]
	}

	response = requests.post(url, headers=headers, data=json.dumps(payload))
	if response.status_code == 200:
		return response.json()
	else:
		return {
			"error": response.text,
			"status_code": response.status_code
		}


# 这里是平台提示词：
PLATFORM_PROMPTS = {
	"wechat_moments": (
		"你是一位专业的社交媒体文案策划师，需要根据我发给你的图片在微信朋友圈帮我生成对应文案。要符合微信朋友圈的用户群体和内容风格：用户群体：熟人社交为主，半私密空间。文案风格：更个人化、偏生活化；可结合亲近口吻、偶尔配点小幽默或感悟。格式要求：无严格字数限制，但过长不利于阅读；表情包和 emoji 可以适度使用，但不宜过多。"
	),
	"xiaohongshu": (
		"你是一位专业的社交媒体文案策划师，需要根据我发给你的图片在小红书帮我生成对应文案。要符合小红书的用户群体和内容风格：用户群体：年轻女性群体居多，关注时尚、美妆、生活方式分享。文案风格：种草/拔草类型的测评，生活化、美好分享型文案，用语相对活泼，善用 emoji 和排版。格式要求：可分段突出重点，适度使用表情符号、感叹号、引导用户点赞收藏。"
	),
	"zhihu": (
		"你是一位专业的社交媒体文案策划师，需要根据我发给你的图片在知乎帮我生成对应文案。要符合知乎的用户群体和内容风格：用户群体：注重知识、经验、思考深度，用户习惯理性表达。文案风格：更专业、逻辑性更强，可结合个人经验或见解，强调干货和实用性。格式要求：可适当分条或引用数据，但要简明扼要，保持“专业可信”的氛围。"
		"可结合一定的知识或见解来丰富文案的深度，语气可适当保持理性和客观。"
	),
	"weibo": (
		"你是一位专业的社交媒体文案策划师，需要根据我发给你的图片在微博帮我生成对应文案。要符合微博的用户群体和内容风格：用户群体：大众社交平台，娱乐、热点话题度较高。文案风格：偏口语化，篇幅不宜过长。可结合热门话题或使用热门标签，带来更多曝光。格式要求：可使用热门话题（#xx#），也可 @相关账号，字数不宜过多；容易被转发、评论、点赞的文案往往带有一些情绪化或话题点。"
		"可以适当使用流行梗或热门话题，语言精简又具备讨论度。"
	),
	"instagram": (
		"你是一位专业的社交媒体文案策划师，需要根据我发给你的图片在instagram帮我生成对应文案。要符合instagram的用户群体和内容风格：用户群体：全球化，视觉导向，主要以图片视频为主，文字通常是对图片的补充。文案风格：更偏向英文或中英结合，对美感和氛围感有要求；常用带话题标签 (#travel, #foodie, #fashion) 来增加可见度。格式要求：文案不要过长；适度加入 emoji、美好体验表达；可以在文案末尾附带一堆主题标签。"
		"关注生活方式，美学和视觉上吸引人的氛围。保持简洁和吸引力。"
	),
	"twitter": (
		"你是一位专业的社交媒体文案策划师，需要根据我发给你的图片在twitter帮我生成对应文案。要符合twitter的用户群体和内容风格：用户群体：全球化，突发新闻、即时信息、行业交流较多。文案风格：简短精炼（280字符限制），常配 hashtag。格式要求：重视话题标签 (@、#)，用简短句子或口号式表达突出观点或情感。 "
		"注意简洁性和有吸引性。"
	),
	"facebook": (
		"你是一位专业的社交媒体文案策划师，需要根据我发给你的图片在facebook帮我生成对应文案。要符合facebook的用户群体和内容风格：用户群体：全球化社交平台，偏成熟用户。可用于商务、品牌推广，也有个人分享。文案风格：比 Twitter 长，但也不宜太长；可带链接或活动信息，引导用户点击或参与。格式要求：可适度带话题标签或链接，也可标注地理位置等；文案适合多语言或国际化表达。"
		"注意友好、对话式风格，同时也可以随意添加一些细节和个人色彩。"
		# Please reply in the following format (be careful not to have any extra text or description) :facebook: [caption content]
	)
}

def get_caption(file_obj, platform: str, user_mood: str = ""):
	"""
	调用 DeepSeek-VL2，用于【生成社交平台文案】场景:
	  1) 将上传的 file_obj 转为 Base64 Data URI
	  2) 根据 platform 和 user_mood 构造合适的 Prompt
	  3) 与大模型通信并返回生成结果

	支持传入单张或多张图片，统一组合后传给大模型综合分析。

	:param file_obj: Flask `request.files` 中获得的 FileStorage 对象或其列表
	:param platform: 用户选择的平台 (如 "wechat_moments", "weibo", "instagram", ...)
	:param user_mood: 用户心情描述 (可选)
	:return: 大模型返回的文案或包含 error 的字典
	"""
	# 如果 file_obj 不是列表，则包装为列表
	# try:
	if not isinstance(file_obj, list):
		data_uri_list = [file_obj]
	else:
		data_uri_list = file_obj

	platforms = ["wechat_moments", "xiaohongshu", "zhihu", "weibo","instagram", "twitter", "facebook"]
	if platform in platforms:
		platform_prompt = PLATFORM_PROMPTS.get(platform, "你是一位专业的社交媒体文案策划师。")
		mood_part = f"用户当前心情：{user_mood}\n" if user_mood.strip() else ""
		text_prompt = (
			f"{platform_prompt}\n\n"
			f"{mood_part}"
			"请结合用户提供的照片内容，写一段吸引人的文案。\n"
			"请按以下格式回复（注意不要带有多余文字或说明）：\n"
			f"社交平台名：[{platform}]\n"
			"文案内容："
		)
	else:
		text_prompt = (
			"你是一位专业的社交媒体文案策划师。\n"
			"请根据此照片内容，生成一段合适的社交媒体文案。\n"
			"请按以下格式回复（注意不要带有多余文字或说明）：\n"
			f"社交平台名：[{platform}]\n"
			"文案内容："
		)

	# 构造消息内容：
	# 根据官方文档示例，第一项为文本提示，后面依次添加每个图片
	# print(text_prompt)
	message_content = [{
		"type": "text",
		"text": text_prompt
	}]
	for data_uri in data_uri_list:
		message_content.append({
			"type": "image_url",
			"image_url": {"url": data_uri}
		})

	#  # 先从Redis拿到旧的对话记录
	# messages = SessionStore.get_messages(session_id)
	# # 把用户这次的请求存进去
	# messages.append({"role": "user", "content": message_content})

	payload = {
		"model": "deepseek-vl2",
		"messages": [
			{
				"role": "user",
				"content": message_content
			}
		]
	}

	# 与大模型通信
	url = Config.WENXIN_API_URL
	headers = {
		"Content-Type": "application/json",
		"Authorization": f"Bearer {Config.WENXIN_API_KEY}",
	}

	# 打印请求的完整内容
	# print("请求的 payload：")
	# print(json.dumps(payload, indent=4))
	resp = requests.post(url, headers=headers, data=json.dumps(payload))

	# 打印返回的内容
	# print("响应内容：")
	# print(resp.json())
	# print("resp")
	# print(resp)

	# 返回原始API响应和message_content
	if resp.status_code == 200:
		return resp.json(), message_content
	else:
		return {"error": resp.text}, message_content
	# except Exception as e:
	# 	print("error"+e)
	# 	traceback.print_exc
	# 	traceback.print_exception
		
	# if resp.status_code == 200:
	# 	result = resp.json()
	# 	# 提取模型返回文本
	# 	# 假设 result["choices"][0]["message"]["content"] 存在
	# 	full_content = result["choices"][0]["message"]["content"]
	# 	# 只取“文案内容：”后面的部分
	# 	parts = full_content.split("文案内容：", 1)
	# 	if len(parts) == 2:
	# 		caption_text = parts[1].strip()
	# 	else:
	# 		caption_text = full_content.strip()

	# 	# 把模型回复也存进去
	# 	messages.append({"role": "assistant", "content": caption_text})
	# 	SessionStore.save_messages(session_id, messages)

	# 	return session_id, caption_text
	# else:
	# 	# 如果大模型出错，依然存入日志，方便排查
	# 	error_msg = f"Error from Wenxin API: {resp.text}"
	# 	messages.append({"role": "assistant", "content": error_msg})
	# 	SessionStore.save_messages(session_id, messages)
	# 	return session_id, error_msg
	
	

def get_caption_with_context(messages):
	"""
	处理多轮对话：用户在已有 session_id 下追加修改需求，让大模型继续调整文案。
    直接传递 `messages`（保留 `image_url` 格式），符合官方 API 格式。
	"""
	# DeepSeek-VL2 API 可以直接使用 messages 结构传递对话历史。

	# conversation_text = ""
	# for m in messages:
	# 	role = m["role"]
	# 	content = m["content"]
	# 	if role == "system":
	# 		conversation_text += f"[系统] {content}\n"
	# 	elif role == "assistant":
	# 		conversation_text += f"[AI] {content}\n"
	# 	else:  # user
	# 		conversation_text += f"[用户] {content}\n"

	# conversation_text += "\n请根据以上对话内容回复新的文案："

	url = Config.WENXIN_API_URL
	headers = {
		"Content-Type": "application/json",
		"Authorization": f"Bearer {Config.WENXIN_API_KEY}",
	}
	payload = {
		"model": "deepseek-vl2",
		"messages": messages  # 直接使用历史对话，不转换成文本
		# "messages": [
		# 	{
		# 		"role": "user",
		# 		"content": [
		# 			{
		# 				"type": "text",
		# 				"text": conversation_text
		# 			}
		# 		]
		# 	}
		# ]
	}

	# resp = requests.post(url, headers=headers, data=json.dumps(payload))
	resp = requests.post(url, headers=headers, json=payload)
	if resp.status_code == 200:
		result = resp.json()
		# return result.get("caption", "")
		return result["choices"][0]["message"]["content"].strip()
	else:
		return {
			"error": resp.text,
			"status_code": resp.status_code
		}
		# raise Exception(f"API Error: {resp.text}")
	
	
# def get_caption(file_obj, platform: str, user_mood: str = ""):
#     """
#     调用 DeepSeek-VL2，用于【生成社交平台文案】场景:
#     1) 将上传的 file_obj 转为 Base64 Data URI
#     2) 根据 platform 和 user_mood 构造合适的 Prompt
#     3) 与大模型通信并返回生成结果

#     :param file_obj:   Flask `request.files` 里获得的FileStorage对象
#     :param platform:   用户选择的平台 (如 "wechat_moments", "weibo", "instagram", ...)
#     :param user_mood:  用户心情描述 (可选)
#     :return: 大模型返回的文案或包含error的字典
#     """
#     # 1) 将 file_obj 转为 Base64 Data URI
#     file_bytes = file_obj.read()
#     content_type = file_obj.mimetype or "image/jpeg"  # 默认用jpeg
#     b64_str = base64.b64encode(file_bytes).decode('utf-8')
#     data_uri = f"data:{content_type};base64,{b64_str}"

#     # 2) 构造 Prompt: 假设你需要告诉大模型 "请针对 xx 平台生成文案"
#     #    你可以写得更详细，如包含平台特点、文案风格等
#     text_prompt = (
#         f"你是一位专业的社交媒体文案策划师，需要根据用户提交的照片，为 {platform} 平台生成一段文案。\n"
#         f"用户当前心情：{user_mood}\n"
#         f"请结合照片内容，写一段吸引人的文案。"
#     )

#     # 3) 与 deepseek-vl2 通信
#     url = Config.WENXIN_API_URL
#     headers = {
#         "Content-Type": "application/json",
#         "Authorization": f"Bearer {Config.WENXIN_API_KEY}",
#     }
#     payload = {
#         "model": "deepseek-vl2",
#         "messages": [
#             {
#                 "role": "user",
#                 "content": [
#                     {
#                         "type": "text",
#                         "text": text_prompt
#                     },
#                     {
#                         "type": "image_url",
#                         "image_url": {
#                             "url": data_uri
#                         }
#                     }
#                 ]
#             }
#         ]
#     }

#     resp = requests.post(url, headers=headers, data=json.dumps(payload))
#     if resp.status_code == 200:
#         # 假设这里返回的也是 .json()，再根据你想要的字段提取
#         result = resp.json()
#         # 你可以只拿它里最关键的 "caption" 或其它
#         return result
#     else:
#         return {
#             "error": resp.text,
#             "status_code": resp.status_code
#         }


