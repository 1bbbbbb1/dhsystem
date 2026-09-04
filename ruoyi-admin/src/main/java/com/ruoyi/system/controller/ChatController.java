package com.ruoyi.system.controller;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.ChatInfo;
import com.ruoyi.system.service.IChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "问答接口")
@RestController
@RequestMapping("/system/chat")
public class ChatController {
    @Autowired
    private IChatService chatService;

    /**
     * 问答接口
     * @param question 提问文本
     * @return 模型回答结果
     */
    @ApiOperation("根据问题获取回答")
    @PostMapping("/ask")
    public AjaxResult askWithChat(
            @ApiParam(value = "提问内容", required = true)
            @RequestParam String question) {
        try {
            String answer = chatService.getChatAnswer(question);
            return AjaxResult.success("查询成功", answer);
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询问答结果接口
     * @param taskId 任务ID
     * @return 任务处理状态及结果
     */
    @ApiOperation("查询问答任务结果")
    @GetMapping("/query/{taskId}")
    public AjaxResult queryChatResult(
            @ApiParam(value = "问答任务ID", required = true)
            @PathVariable String taskId) {
        try {
            // 调用服务层查询方法，获取任务结果
            Map<String, Object> result = chatService.queryChatResult(taskId);
            return AjaxResult.success("查询成功", result);
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("创建新会话")
    @PostMapping("/session")
    public AjaxResult createSession() {
        try {
            String sessionId = chatService.createSession();
            return AjaxResult.success("会话创建成功", sessionId);
        } catch (Exception e) {
            return AjaxResult.error("会话创建失败：" + e.getMessage());
        }
    }

    @ApiOperation("发送消息")
    @PostMapping("/send")
    public AjaxResult sendMessage(
            @ApiParam(value = "会话ID", required = true) @RequestParam String sessionId,
            @ApiParam(value = "消息内容", required = true) @RequestParam String message) {
        try {
            String taskId = chatService.sendMessage(sessionId, message);
            return AjaxResult.success("消息发送成功", taskId);
        } catch (Exception e) {
            return AjaxResult.error("消息发送失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取会话历史")
    @GetMapping("/history/{sessionId}")
    public AjaxResult getSessionHistory(@PathVariable String sessionId) {
        try {
            List<ChatInfo> history = chatService.getSessionHistory(sessionId);
            return AjaxResult.success("获取历史成功", history);
        } catch (Exception e) {
            return AjaxResult.error("获取历史失败：" + e.getMessage());
        }
    }
}
