# app.py
from flask import Flask
from AICameraBackend.config import Config
from AICameraBackend.routes.photo import photo_bp
from AICameraBackend.routes.caption import caption_bp
# import jsonify

def create_app():
	app = Flask(__name__)
	app.config.from_object(Config)

	 # 配置日志（新增代码）
	import logging
	logging.basicConfig(level=logging.DEBUG)
	handler = logging.StreamHandler()
	handler.setLevel(logging.DEBUG)
	app.logger.addHandler(handler)

	# # 注册全局错误处理器（新增代码）
	# @app.errorhandler(Exception)
	# def handle_exception(e):
	# 	# 打印错误堆栈到控制台（关键）
	# 	import traceback
	# 	traceback.print_exc()  
	# 	# 返回JSON错误信息（可选）
	# 	return jsonify({
	# 		"error": str(e),
	# 		"traceback": traceback.format_exc()
	# 	}), 500

	# 注册路由蓝图
	app.register_blueprint(photo_bp, url_prefix='/photo')
	@app.route("/")
	def index():
		return "Hello, This is the Photo App with Flask!"


	app.register_blueprint(caption_bp, url_prefix='/caption')

	# print("URL Map:", app.url_map)
	return app



if __name__ == '__main__':
	app = create_app()
	app.run(debug=True, host='0.0.0.0', port=5000)
	
