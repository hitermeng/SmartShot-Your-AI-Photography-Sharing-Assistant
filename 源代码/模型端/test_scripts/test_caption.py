# test_caption.py

import requests

def test_generate_captions(server_url, platform, image_paths, user_mood=""):
    """
    测试向后端 /generate 接口发送图片和参数，获取文案生成结果
    :param server_url: 后端服务器地址，如 "http://127.0.0.1:5000"
    :param platform:   需要测试的平台，如 "wechat_moments"
    :param image_paths: list, 一组本地图片文件路径
    :param user_mood:  用户心情描述
    :return: None
    """
    url = f"{server_url}/generate"

    for img_path in image_paths:
        print(f"\n[TEST] Sending {img_path} to {platform} ...")
        with open(img_path, 'rb') as f:
            # data 里放 form 表单字段
            # files 里放文件
            data = {
                'platform': platform,
                'user_mood': user_mood
            }
            files = {
                'file': f
            }

            try:
                resp = requests.post(url, data=data, files=files)
                if resp.status_code == 200:
                    result = resp.json()
                    print("[SUCCESS] Response:", result)
                else:
                    print("[ERROR] HTTP", resp.status_code, resp.text)
            except Exception as e:
                print("[EXCEPTION]", e)

def test_multi_round_dialogue(server_url, platform, image_path, user_mood=""):
    """
    测试多轮对话生成文案:
    1) 调用 /generate 生成初版文案并返回 session_id
    2) 调用 /conversation/update 多次, 每次带着 session_id + new_request
    3) 查看多次改写后的文案
    """
    generate_url = f"{server_url}/generate"
    update_url = f"{server_url}/conversation_update"

    print(f"\n[TEST MULTI-ROUND] Start conversation with: {platform}, image={image_path}")
    with open(image_path, 'rb') as f:
        data = {
            'platform': platform,
            'user_mood': user_mood
        }
        files = {
            'file': f
        }
        resp = requests.post(generate_url, data=data, files=files)
        if resp.status_code == 200:
            first_result = resp.json()
            print("[STEP1] First round response:", first_result)
        else:
            print("[ERROR] HTTP", resp.status_code, resp.text)
            return

    # 从first_result里获取 session_id 和初版文案
    session_id = first_result.get("session_id", "")
    if not session_id:
        print("[ERROR] No session_id returned. Cannot continue multi-round test.")
        return

    # ---- 以下模拟用户多次提出新的改写需求 ----
    # 你可以自由添加多轮，示例做两轮

    # 第2轮: 让文案更幽默点
    new_request_1 = "请再改得幽默一些，还要更口语化一点"
    data_update_1 = {
        "session_id": session_id,
        "new_request": new_request_1
    }
    resp_2 = requests.post(update_url, json=data_update_1)
    if resp_2.status_code == 200:
        result_2 = resp_2.json()
        print("[STEP2] Update response:", result_2)
    else:
        print("[ERROR] HTTP", resp_2.status_code, resp_2.text)
        return

    # 第3轮: 让文案中加入一些励志风格
    new_request_2 = "再加一点励志的句子"
    data_update_2 = {
        "session_id": session_id,
        "new_request": new_request_2
    }
    resp_3 = requests.post(update_url, json=data_update_2)
    if resp_3.status_code == 200:
        result_3 = resp_3.json()
        print("[STEP3] Update response:", result_3)
    else:
        print("[ERROR] HTTP", resp_3.status_code, resp_3.text)
        return
    

if __name__ == "__main__":
    # 假设你的 Flask 服务在本地 5000 端口
    server_url = "http://127.0.0.1:5000/caption"

    # 要测试的平台
    platforms_to_test = [
        "wechat_moments",
        "xiaohongshu",
        "zhihu",
        "weibo",
        "instagram",
        "twitter",
        "facebook"
    ]

    # 测试用的本地图像路径(你可以放一张或多张测试图)
    test_images = [
        # './static/test_images/sample_0.jpg',
        './static/test_images/sample_1003.jpg'

    ]

    # 你也可以设置不同的心情，测试看看对文案是否有影响
    user_mood = "今天心情特别放松愉悦"

    # 逐个平台、逐个图片测试
    for p in platforms_to_test:
        test_generate_captions(server_url, p, test_images, user_mood)

	# # 也可以只测试一个平台
    # test_generate_captions(server_url, platforms_to_test[0], test_images, user_mood)

	# 2) 演示多轮对话
    # 注意只挑一个平台 & 一张图，看看多轮流程
    test_multi_round_dialogue(server_url, "wechat_moments", "./static/test_images/sample_1003.jpg", user_mood)