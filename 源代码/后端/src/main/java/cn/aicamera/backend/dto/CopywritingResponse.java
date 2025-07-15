package cn.aicamera.backend.dto;

public class CopywritingResponse {
    private String caption;
    private String sessionId;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public CopywritingResponse(String caption, String sessionId) {
        this.caption = caption;
        this.sessionId = sessionId;
    }
}
