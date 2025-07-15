package cn.aicamera.backend.exception;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String message) {
        super(401, message); // 401 表示未授权
    }
}
