package com.ruoyi.system.service;
import com.ruoyi.system.domain.ChatInfo;
import java.util.List;
import java.util.Map;

public interface IChatService {
    // 新增会话
    String createSession();

    // 发送消息（带会话ID）
    String sendMessage(String sessionId, String question) throws Exception;

    // 查询会话历史
    List<ChatInfo> getSessionHistory(String sessionId);

//    @Override
    String getChatAnswer(String question) throws Exception;

//    @Override
    Map<String, Object> queryChatResult(String taskId) throws Exception;
}
