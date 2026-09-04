package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ChatInfo;
import com.ruoyi.system.domain.ChatSession;
import com.ruoyi.system.domain.ChatTaskInfo;
import com.ruoyi.system.service.IChatService;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class IChatServiceImpl implements IChatService {
    // 从配置文件读取API密钥
    @Value("${ark.api.key}")
    private String apiKey;

    // 从配置文件读取模型ID（默认使用示例中的模型）
    @Value("${ark.vision.model:doubao-seed-1-6-251015}")
    private String modelId;

    // 接口基础URL（从配置文件读取，默认北京地域）
    @Value("${ark.base.url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    private ArkService arkService;

    // 用于存储任务状态的缓存（实际项目中建议使用Redis）
    private final ConcurrentHashMap<String, ChatTaskInfo> taskCache = new ConcurrentHashMap<>();
    // 线程池用于处理异步任务
    private final ExecutorService executor = new ThreadPoolExecutor(
            5, 10, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "chat-task-" + counter.getAndIncrement());
                }
            }
    );

    // 初始化Ark服务（项目启动时执行）
    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("未配置ARK_API_KEY，请在application.yml中设置");
        }

        // 初始化连接池（复用示例中的配置）
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();

        // 构建服务实例
        arkService = ArkService.builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    // 新增会话缓存
    private final ConcurrentHashMap<String, ChatSession> sessionCache = new ConcurrentHashMap<>();

    // 创建新会话
    @Override
    public String createSession() {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        ChatSession session = new ChatSession();
        session.setSessionId(sessionId);
        session.setMessages(new ArrayList<>());
        session.setLastActiveTime(new Date());
        sessionCache.put(sessionId, session);
        return sessionId;
    }

    // 发送消息（核心修改）
    @Override
    public String sendMessage(String sessionId, String question) throws Exception {

        // 获取会话
        ChatSession session = sessionCache.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("无效的会话ID");
        }

        // 添加用户消息到历史
        ChatInfo userMessage = new ChatInfo();
        userMessage.setRole(ChatMessageRole.valueOf(("USER")));
        userMessage.setContent(question);
        userMessage.setTimestamp(new Date());
        session.getMessages().add(userMessage);

        // 生成任务ID
        String taskId = UUID.randomUUID().toString().replace("-", "");
        ChatTaskInfo taskInfo = new ChatTaskInfo();
        // ... 任务初始化
        taskInfo.setTaskId(taskId);
        taskInfo.setStatus("processing");
        taskInfo.setProgress(0);
        taskCache.put(taskId, taskInfo);

        // 异步处理时携带完整对话历史
        executor.submit(() -> {
            try {
                // 构建包含历史消息的请求
                List<ChatMessage> messages = new ArrayList<>();
                for (ChatMessage msg : session.getMessages()) {
                    messages.add(ChatMessage.builder()
                            .role(msg.getRole().equals(ChatMessageRole.USER) ? ChatMessageRole.USER : ChatMessageRole.ASSISTANT)
                            .content((String) msg.getContent())
                            .build());
                }

                // 调用AI接口获取回答
                String answer = doChatRequestWithHistory(messages);

                // 添加助手回复到历史
                ChatInfo assistantMessage = new ChatInfo();
                assistantMessage.setRole(ChatMessageRole.valueOf("ASSISTANT"));
                assistantMessage.setContent(answer);
                assistantMessage.setTimestamp(new Date());
                session.getMessages().add(assistantMessage);

                // 更新任务状态
                taskInfo.setStatus("succeeded");
                taskInfo.setAnswer(answer);
            } catch (Exception e) {
                // 错误处理
            }
        });

        return taskId;
    }

    // 获取会话历史
    @Override
    public List<ChatInfo> getSessionHistory(String sessionId) {
        ChatSession session = sessionCache.get(sessionId);
        return session != null ? session.getMessages() : new ArrayList<>();
    }

    // 带历史的AI请求方法
    private String doChatRequestWithHistory(List<ChatMessage> messages) throws Exception {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelId)
                .messages(messages)
                .build();

        return arkService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent().toString();
    }

    @Override
    public String getChatAnswer(String question) throws Exception {
        // 生成唯一任务ID
        String taskId = UUID.randomUUID().toString().replace("-", "");

        // 初始化任务信息
        ChatTaskInfo taskInfo = new ChatTaskInfo();
        taskInfo.setTaskId(taskId);
        taskInfo.setStatus("processing");
        taskInfo.setProgress(0);
        taskCache.put(taskId, taskInfo);

        // 提交异步任务处理
        executor.submit(() -> {
            try {
                // 模拟处理进度
                for (int i = 10; i <= 100; i += 10) {
                    taskInfo.setProgress(i);
                    Thread.sleep(500); // 模拟处理耗时
                }

                // 执行实际的AI调用
                String answer = doChatRequest(question);

                // 更新任务状态为成功
                taskInfo.setStatus("succeeded");
                taskInfo.setAnswer(answer);
                taskInfo.setProgress(100);
            } catch (Exception e) {
                // 更新任务状态为失败
                taskInfo.setStatus("failed");
                taskInfo.setErrorMsg(e.getMessage());
                taskInfo.setProgress(100);
            }
        });

        // 返回任务ID给前端，用于轮询查询
        return taskId;
    }

    @Override
    public Map<String, Object> queryChatResult(String taskId) throws Exception {
        // 从缓存获取任务信息
        ChatTaskInfo taskInfo = taskCache.get(taskId);
        if (taskInfo == null) {
            throw new IllegalArgumentException("无效的任务ID: " + taskId);
        }

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskId);
        result.put("status", taskInfo.getStatus());
        result.put("progress", taskInfo.getProgress());
        result.put("answer", taskInfo.getAnswer());
        result.put("errorMsg", taskInfo.getErrorMsg());

        // 清理已完成的任务（可选，根据业务需求）
        if ("succeeded".equals(taskInfo.getStatus()) || "failed".equals(taskInfo.getStatus())) {
            // 延迟清理，避免前端最后一次查询失败
            executor.submit(() -> {
                try {
                    Thread.sleep(300000); // 5分钟后清理
                    taskCache.remove(taskId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        return result;
    }

    // 实际执行AI请求的方法
    private String doChatRequest(String question) throws Exception {
        List<ChatMessage> messages = new ArrayList<>();
        List<ChatCompletionContentPart> multiParts = new ArrayList<>();

        // 添加文本问题
        multiParts.add(ChatCompletionContentPart.builder()
                .type("text")
                .text(question)
                .build());

        // 构建用户消息
        ChatMessage userMessage = ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .multiContent(multiParts)
                .build();
        messages.add(userMessage);

        // 构建请求参数
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelId)
                .messages(messages)
                .build();

        // 调用API并返回结果
        return arkService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent().toString();
    }
}
