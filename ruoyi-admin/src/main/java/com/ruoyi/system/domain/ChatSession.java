package com.ruoyi.system.domain;
import java.util.Date;
import java.util.List;

public class ChatSession {
    private String sessionId; // 会话ID
    private List<ChatInfo> messages; // 消息列表
    private Date lastActiveTime; // 最后活跃时间

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<ChatInfo> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatInfo> messages) {
        this.messages = messages;
    }

    public Date getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(Date lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }
}
