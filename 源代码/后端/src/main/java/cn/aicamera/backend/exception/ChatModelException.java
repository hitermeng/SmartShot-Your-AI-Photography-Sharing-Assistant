package cn.aicamera.backend.exception;

public class ChatModelException extends CustomException{
    public ChatModelException(String message){
        super(503,message);
    }
}
