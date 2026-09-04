package com.ruoyi.system.controller;

import com.volcengine.ark.runtime.model.images.generation.ImagesResponse;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IImageGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "图片生成接口")
@RestController
@RequestMapping("/system/image")
public class ImageGenerationController {

    @Autowired
    private IImageGenerationService imageGenerationService;

    /**
     * 生成图片接口
     * @param prompt 图片描述（必填）
     * @param size 图片尺寸（可选，默认2K）
     * @return 图片URL或错误信息
     */
    @ApiOperation("根据提示词生成图片")
    @PostMapping("/generate")
    public AjaxResult generateImage(
            @ApiParam(value = "图片描述（例如：生成一只猫在太空的图片）", required = true)
            @RequestParam String prompt,
            @ApiParam(value = "图片尺寸（可选值：2K、4K等）", defaultValue = "2K")
            @RequestParam(required = false, defaultValue = "2K") String size) {
        try {
            // 调用服务生成图片
            ImagesResponse response = imageGenerationService.generateImage(prompt, size);

            // 处理响应结果
            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                String imageUrl = response.getData().get(0).getUrl();
                return AjaxResult.success("图片生成成功", imageUrl);
            } else {
                return AjaxResult.error("未生成任何图片");
            }
        } catch (Exception e) {
            return AjaxResult.error("图片生成失败：" + e.getMessage());
        }
    }
}
