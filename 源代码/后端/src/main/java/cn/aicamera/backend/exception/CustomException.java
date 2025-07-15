package cn.aicamera.backend.exception;

/**
 * 异常的基类
 */
public class CustomException extends RuntimeException {
    private final int code; // 自定义错误码
    private final String message; // 错误信息

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
