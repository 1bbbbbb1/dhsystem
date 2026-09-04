package com.ruoyi.system.service;

import com.volcengine.ark.runtime.model.images.generation.ImagesResponse;

public interface IImageGenerationService {
    /**
     * 根据提示词生成图片
     * @param prompt 图片描述提示词
     * @param size 图片尺寸（如"2K"）
     * @return 图片生成响应结果（包含URL等信息）
     * @throws Exception 生成过程中的异常
     */
    ImagesResponse generateImage(String prompt, String size) throws Exception;
}
