# routes/photo.py
from flask import Blueprint, request, jsonify
from AICameraBackend.services.ai_service import call_wenxin_api
# from services.ai_service import call_wenxin_api
import os
import base64
import re
import json

photo_bp = Blueprint("photo_bp", __name__)

@photo_bp.route("/live_preview", methods=["POST"])
def live_preview():
	"""
	以JSON方式接收前端数据: 
	{
	  "user_text": "...",
	  "image_url": "base64或真实url"
	}
	调用 call_wenxin_api, 返回大模型结果
	"""
	# ===== 优先判断: 是否是multipart/form-data? =====
	if request.content_type.startswith("multipart/form-data"):  
		# 说明是Spring Boot那边以 multipart/form-data 发送的文件
		data_uri = request.form.get("image", "")
		if not data_uri:
			return jsonify({"error": "No base64 image data found in form['image']"}), 400

		# 可以从表单字段/headers里获取额外信息, 比如 user_text, 但要用request.form.get()
		# 如果没有则用一个默认Prompt
		text_prompt = request.form.get("user_text", 
			default="你是一位专业摄影师与构图专家,根据我拍的这张图片,从美学的角度给我这张照片打分(满分100分)。"
					+"同时我该怎么移动我的手机,可以构图更好?请给出移动手机的具体数值化建议。"
					+"请只输出 JSON 格式，包含以下字段：\"grade\": 美学评分(满分100分)\\n\"moveUpDown\": 上下移动距离(单位：厘米)，正值为手机上移，负值为手机下移，0为不需要手机上下移动"
					+"\\n\"moveLeftRight\": 左右移动距离(单位：厘米)，正值为手机右移、负值为手机左移，0为不需要手机左右移动\\n\"moveForwardBackward\": 前后移动距离(单位：厘米)，正值为手机向前移动，"
					 "负值为手机向下移动，0为不需要手机上下移动")
		

		# 调用大模型API
		result = call_wenxin_api(text_prompt, data_uri)
		# print("Status code:", response.status_code)
		print("Response:", result)
		# return jsonify({"answer": result})


# 从result中提取choices里返回的内容
		try:
			message_content = result["choices"][0]["message"]["content"]
			# 去除 markdown 的 ```json 和 ``` 标记
			# 使用正则匹配内容中的JSON部分
			m = re.search(r"```json\s*(\{.*?\})\s*```", message_content, re.DOTALL)
			if m:
				json_str = m.group(1)
			else:
				# 如果没有匹配到，则直接去除前后反引号
				json_str = message_content.strip("`").strip()
			# 解析JSON字符串
			output_data = json.loads(json_str)
		except Exception as e:
			return jsonify({"error": "解析返回内容失败", "detail": str(e)}), 500

		# 返回只包含 grade, moveUpDown, moveLeftRight, moveForwardBackward 的 JSON 结果
		return jsonify(output_data)
	
	# return jsonify({"error": "Unsupported content type"}), 400

		# return result
	
	else:
		print("json")
		# ===== 否则尝试用JSON方式读取 =====
		data = request.get_json()
		if not data:
			return jsonify({"error": "No file in 'image' nor JSON body found"}), 400

		text_prompt = data.get("user_text", 
			"你是一位专业摄影师与构图专家,根据我拍的这张图片,从美学的角度给我这张照片打分(满分100分)。"
					+"同时我该怎么移动我的手机,可以构图更好?请给出移动手机的具体数值化建议。"
					+"请只输出 JSON 格式，包含以下字段：\"grade\": 美学评分(满分100分)\\n\"moveUpDown\": 上下移动距离(单位：厘米)，正值为上移"
					+"\\n\"moveLeftRight\": 左右移动距离(单位：厘米)，正值为右移\\n\"moveForwardBackward\": 前后移动距离(单位：厘米)，正值为向前")

		image_url = data.get("image_url", "")
		# image_url 可能是:
		# 1) "http://xxx.jpg" 这样的URL
		# 2) "data:image/jpeg;base64,AAAA..." 这样的Base64 Data URI
		# 在这里, 如果你还想再做某些转换(如重新转成 base64), 可视需求处理
		# 否则直接传给大模型

		result = call_wenxin_api(text_prompt, image_url)
		return jsonify({"answer": result})


	# data = request.get_json()
	# if not data:
	#     return jsonify({"error": "No JSON provided"}), 400
	
	# text_prompt = data.get("user_text", "你是一位专业摄影师与构图专家,根据我拍的这张图片,从美学的角度给我这张照片打分(满分100分)。"
	# 				+"同时我该怎么移动我的手机,可以构图更好?请给出移动手机的具体数值化建议。"
	# 				+"请只输出 JSON 格式，包含以下字段：\"grade\": 美学评分(满分100分)\\n\"moveUpDown\": 上下移动距离(单位：厘米)，正值为上移"
	# 				+"\\n\"moveLeftRight\": 左右移动距离(单位：厘米)，正值为右移\\n\"moveForwardBackward\": 前后移动距离(单位：厘米)，正值为向前")
	# image_url = data.get("image_url", "")

	# # 调用大模型API
	# result = call_wenxin_api(text_prompt, image_url)

	# # 将结果直接JSON返回
	# return jsonify({"answer": result})



@photo_bp.route('/final_capture', methods=['POST'])
def final_capture():
	"""
	用户正式拍下照片后调用此接口
	- 可再次做一次美学评分(可选)
	- 或直接保存成片
	"""
	uploaded_file = request.files.get('file')
	if not uploaded_file:
		return jsonify({"error": "No file uploaded"}), 400

	# 保存照片(可选)
	save_dir = "./static/final_photos"
	os.makedirs(save_dir, exist_ok=True)
	save_path = os.path.join(save_dir, uploaded_file.filename)
	uploaded_file.save(save_path)

	# 也可在此再调 get_preview_analysis 看最终评分
	# beauty_score, move_suggestion = get_preview_analysis(uploaded_file)

	return jsonify({
		"message": "Final photo saved successfully",
		"file_path": save_path,
		# "beauty_score": beauty_score,
		# "move_suggestion": move_suggestion
	}), 200




