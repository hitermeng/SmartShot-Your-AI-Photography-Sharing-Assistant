package cn.aicamera.backend.exception;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(String message) {
        super(404, message); // 404 表示资源未找到
    }
}
