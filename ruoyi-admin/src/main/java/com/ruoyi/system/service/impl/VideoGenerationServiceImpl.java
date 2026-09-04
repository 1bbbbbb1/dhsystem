package com.ruoyi.system.service.impl;

import com.volcengine.ark.runtime.model.content.generation.CreateContentGenerationTaskRequest;
import com.volcengine.ark.runtime.model.content.generation.CreateContentGenerationTaskResult;
import com.volcengine.ark.runtime.model.content.generation.GetContentGenerationTaskRequest;
import com.volcengine.ark.runtime.model.content.generation.GetContentGenerationTaskResponse;
import com.volcengine.ark.runtime.service.ArkService;
import com.ruoyi.system.service.IVideoGenerationService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class VideoGenerationServiceImpl implements IVideoGenerationService {
    // 从配置文件读取API密钥（避免硬编码）
    @Value("${ark.api.key}")
    private String apiKey;

    // 视频生成模型ID（可配置在application.yml中）
    @Value("${ark.video.model:doubao-seedance-1-0-lite-t2v-250428}")
    private String videoModel;

    private ArkService arkService;

    // 初始化ArkService（项目启动时执行）
    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("未配置ARK_API_KEY，请在application.yml中设置");
        }

        // 初始化连接池（复用示例中的配置）
        ConnectionPool connectionPool = new ConnectionPool(
                5,  // 最大空闲连接数
                1,   // 连接空闲超时时间（秒）
                TimeUnit.SECONDS
        );
        Dispatcher dispatcher = new Dispatcher();

        // 构建服务实例
        arkService = ArkService.builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .apiKey(apiKey)
                .build();
    }

    @Override
    public String createVideoTask(String prompt) throws Exception {
        // 构建视频生成请求参数
        List<CreateContentGenerationTaskRequest.Content> contents = new ArrayList<>();
        contents.add(CreateContentGenerationTaskRequest.Content.builder()
                .type("text")
                .text(prompt)  // 提示词包含所有参数（如--resolution --ratio --duration等）
                .build());

        // 创建视频生成任务
        CreateContentGenerationTaskRequest createRequest = CreateContentGenerationTaskRequest.builder()
                .model(videoModel)
                .content(contents)
                .build();

        CreateContentGenerationTaskResult createResult = arkService.createContentGenerationTask(createRequest);
        return createResult.getId();  // 返回任务ID，供前端查询状态
    }

    @Override
    public GetContentGenerationTaskResponse getVideoTaskStatus(String taskId) throws Exception {
        // 构建任务查询请求
        GetContentGenerationTaskRequest getRequest = GetContentGenerationTaskRequest.builder()
                .taskId(taskId)
                .build();
        return arkService.getContentGenerationTask(getRequest);
    }
}
