package com.ruoyi.system.service.impl;

import com.volcengine.ark.runtime.model.images.generation.GenerateImagesRequest;
import com.volcengine.ark.runtime.model.images.generation.ImagesResponse;
import com.volcengine.ark.runtime.model.images.generation.ResponseFormat;
import com.volcengine.ark.runtime.service.ArkService;
import com.ruoyi.system.service.IImageGenerationService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class ImageGenerationServiceImpl implements IImageGenerationService {
    // 从配置文件读取API密钥（避免硬编码）
    @Value("${ark.api.key}")
    private String apiKey;

    private ArkService arkService;

    // 初始化ArkService（项目启动时执行）
    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("未配置ARK_API_KEY，请在application.yml中设置");
        }

        // 初始化连接池
        ConnectionPool connectionPool = new ConnectionPool(
                10, // 最大空闲连接数
                5,  // 连接空闲超时时间（秒）
                TimeUnit.SECONDS
        );
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(20); // 最大并发请求数
        dispatcher.setMaxRequestsPerHost(10); // 每个主机最大并发数

        // 构建服务实例
        arkService = ArkService.builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .apiKey(apiKey)
                // .timeout(30, TimeUnit.SECONDS)
                // 设置超时时间
                .build();
    }

    @Override
    public ImagesResponse generateImage(String prompt, String size) throws Exception {
        // 构建请求参数
        GenerateImagesRequest.SequentialImageGenerationOptions sequentialOpts =
                new GenerateImagesRequest.SequentialImageGenerationOptions();
        sequentialOpts.setMaxImages(1);

        GenerateImagesRequest generateRequest = GenerateImagesRequest.builder()
                .model("doubao-seedream-4-0-250828")
                .prompt(prompt)
                .responseFormat(ResponseFormat.Url)
                .size(size)
                .sequentialImageGeneration("auto")
                .sequentialImageGenerationOptions(sequentialOpts)
                .stream(false)
                .watermark(false)
                .build();

        // 调用API生成图片
        return arkService.generateImages(generateRequest);
    }
}
