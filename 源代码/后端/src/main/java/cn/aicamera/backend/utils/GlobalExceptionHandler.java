package cn.aicamera.backend.utils;

import cn.aicamera.backend.dto.GeneralResponse;
import cn.aicamera.backend.dto.SuccessResponse;
import cn.aicamera.backend.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 处理全局异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<SuccessResponse> handleCustomException(CustomException ex) {
        SuccessResponse errorResponse = new SuccessResponse(false, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getCode()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<GeneralResponse> handleJwtExpiredException(ExpiredJwtException ex){
        GeneralResponse<String> errorResponse = new GeneralResponse<>(false,"未登录或登录已过期",null);
        return new ResponseEntity<>(errorResponse,HttpStatus.valueOf(200));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.badRequest().body("文件大小超过限制，最大允许 10MB");
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<SuccessResponse> handleException(Exception ex) {
//        SuccessResponse errorResponse = new SuccessResponse(false, "服务器内部错误");
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
