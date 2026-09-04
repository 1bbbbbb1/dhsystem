package com.ruoyi.system.controller;

import com.volcengine.ark.runtime.model.content.generation.GetContentGenerationTaskResponse;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IVideoGenerationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "视频生成接口")
@RestController
@RequestMapping("/system/video")
public class VideoGenerationController {

    @Autowired
    private IVideoGenerationService videoGenerationService;

    /**
     * 创建视频生成任务
     * @param prompt 视频描述提示词（需包含参数，如："写实风格... --ratio 16:9 --duration 5"）
     * @return 任务ID（用于查询状态）
     */
    @ApiOperation("创建视频生成任务")
    @PostMapping("/createTask")
    public AjaxResult createVideoTask(
            @ApiParam(value = "视频描述及参数（例如：晴朗的蓝天... --ratio 16:9 --duration 5）", required = true)
            @RequestParam String prompt) {
        try {
            String taskId = videoGenerationService.createVideoTask(prompt);
            return AjaxResult.success("视频生成任务创建成功", taskId);
        } catch (Exception e) {
            return AjaxResult.error("任务创建失败：" + e.getMessage());
        }
    }

    /**
     * 查询视频任务状态
     * @param taskId 任务ID（从创建接口获取）
     * @return 任务状态及结果（如视频URL）
     */
    @ApiOperation("查询视频生成任务状态")
    @GetMapping("/queryTask")
    public AjaxResult queryVideoTask(
            @ApiParam(value = "视频任务ID", required = true)
            @RequestParam String taskId) {
        try {
            GetContentGenerationTaskResponse response = videoGenerationService.getVideoTaskStatus(taskId);
            return AjaxResult.success("任务查询成功", response);
        } catch (Exception e) {
            return AjaxResult.error("任务查询失败：" + e.getMessage());
        }
    }
}
