import os,base64, requests,json

def main():
	# 获取脚本本身所在的目录
	base_dir = os.path.dirname(os.path.abspath(__file__))
	# 组合出图片的绝对路径
	img_path = os.path.join(base_dir, "..", "static", "test_images", "sample_0.jpg")

	with open(img_path, "rb") as f:
	# with open("../static/test_images/sample_0.jpg", "rb") as f:
		img_data = f.read()
	b64_str = base64.b64encode(img_data).decode("utf-8")
	# 拼成官方文档需要的格式：data:image/<格式>;base64,<Base64编码>
	data_uri = f"data:image/jpeg;base64,{b64_str}"

	payload = {
	# "user_text": "你是一位专业摄影师与构图专家,根据我拍的这张图片,从美学的角度给我这张照片打分(满分100分)。"
	# 				"同时我该怎么移动我的手机,可以构图更好?请给出移动手机的具体数值化建议。"
	# 				"请只输出 JSON 格式，包含以下字段：\"grade\": 美学评分(满分100分)\\n\"moveUpDown\": 上下移动距离(单位：厘米)，正值为上移"
	# 				"\\n\"moveLeftRight\": 左右移动距离(单位：厘米)，正值为右移\\n\"moveForwardBackward\": 前后移动距离(单位：厘米)，正值为向前",
	"image_url": data_uri
    }

		# "user_text": "我该怎么移动我的手机,可以构图更好?拍出更美的照片?",
		# "image_url": "https://example.com/path/to/your_image.jpg"

	# 2. 发送POST请求到你本地/服务器的Flask接口
	url = "http://127.0.0.1:5000/photo/live_preview"
	response = requests.post(url, json=payload)

	# 3. 打印结果
	print("Status code:", response.status_code)
	print("Response:", response.json())

if __name__ == "__main__":
	main()
