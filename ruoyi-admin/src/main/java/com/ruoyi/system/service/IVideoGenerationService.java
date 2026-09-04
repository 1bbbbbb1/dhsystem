package com.ruoyi.system.service;

import com.volcengine.ark.runtime.model.content.generation.GetContentGenerationTaskResponse;

public interface IVideoGenerationService {
    /**
     * 创建视频生成任务
     * @param prompt 视频描述提示词（包含参数，如--ratio 16:9等）
     * @return 任务ID
     * @throws Exception 生成过程中的异常
     */
    String createVideoTask(String prompt) throws Exception;

    /**
     * 查询视频生成任务状态
     * @param taskId 任务ID
     * @return 任务详情（包含状态、视频URL等）
     * @throws Exception 查询过程中的异常
     */
    GetContentGenerationTaskResponse getVideoTaskStatus(String taskId) throws Exception;
}
