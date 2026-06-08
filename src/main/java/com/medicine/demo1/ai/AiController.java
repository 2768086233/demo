package com.medicine.demo1.ai;

import com.medicine.demo1.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Tag(name = "AI助手", description = "AI 问答对话")
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String API_KEY = "sk-d2aa20a3c6924ed9891880417eb0eccc";
    private static final String MODEL = "qwen3.5-35b-a3b";

    private final RestTemplate restTemplate = new RestTemplate();

    @Operation(summary = "AI 对话")
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        String userMsg = request.getMessage();
        if (userMsg == null || userMsg.trim().isEmpty()) {
            return Result.failed("请输入消息");
        }

        // 构建消息列表
        List<Map<String, String>> messages = new ArrayList<>();

        // 系统提示词 - 药品助手场景
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一个智能药品管理助手，帮助用户管理家庭药品、解答用药疑问。" +
                "你可以回答关于药品有效期、用法用量、药品分类、常见疾病用药建议等问题。" +
                "如果用户问的是非医疗相关问题，你也可以友好地回答。" +
                "请用中文回答，语言简洁易懂。");
        messages.add(systemMsg);

        // 历史消息
        if (request.getHistory() != null) {
            for (ChatMessage msg : request.getHistory()) {
                Map<String, String> m = new HashMap<>();
                m.put("role", msg.getRole());
                m.put("content", msg.getContent());
                messages.add(m);
            }
        }

        // 当前用户消息
        Map<String, String> userMap = new HashMap<>();
        userMap.put("role", "user");
        userMap.put("content", userMsg);
        messages.add(userMap);

        // 构建请求体
        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("messages", messages);
        body.put("stream", false);
        body.put("max_tokens", 1024);
        body.put("temperature", 0.7);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            log.info("正在请求 AI API...");
            org.springframework.http.HttpStatus statusCode;
            String responseBody;

            try {
                ResponseEntity<String> rawResponse = restTemplate.postForEntity(API_URL, entity, String.class);
                statusCode = (org.springframework.http.HttpStatus) rawResponse.getStatusCode();
                responseBody = rawResponse.getBody();
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                // 4xx 错误（如 403）需要读取响应体
                statusCode = (org.springframework.http.HttpStatus) e.getStatusCode();
                responseBody = e.getResponseBodyAsString();
            }

            log.info("AI API 状态码: {}, 响应: {}", statusCode, responseBody);

            if (statusCode.is2xxSuccessful() && responseBody != null) {
                // 解析 JSON 提取回复
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Map<String, Object> respMap = mapper.readValue(responseBody, Map.class);

                if (respMap.containsKey("choices")) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) respMap.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> choice = choices.get(0);
                        Map<String, String> message = (Map<String, String>) choice.get("message");
                        String reply = message.get("content");
                        log.info("AI 回复成功");
                        ChatResponse chatResponse = new ChatResponse();
                        chatResponse.setReply(reply);
                        return Result.success(chatResponse);
                    }
                }
            }

            // 提取具体错误信息
            String errorMsg = "AI 服务异常，请稍后重试";
            if (responseBody != null && responseBody.contains("error")) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    Map<String, Object> errMap = mapper.readValue(responseBody, Map.class);
                    if (errMap.containsKey("error")) {
                        Map<String, Object> errorDetail = (Map<String, Object>) errMap.get("error");
                        String code = (String) errorDetail.get("code");
                        String message = (String) errorDetail.get("message");
                        if ("access denied".equals(code)) {
                            errorMsg = "API Key 无权限访问该模型。请在阿里云百炼平台开通 deepseek-r1-distill-llama-70b 模型，或检查 API Key 是否有效。";
                        } else {
                            errorMsg = "AI 服务错误: " + (message != null ? message : code);
                        }
                    }
                } catch (Exception ignored) {}
            }
            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setReply("🤖 " + errorMsg);
            return Result.success(chatResponse);
        } catch (Exception e) {
            log.error("AI API 调用失败", e);
            ChatResponse fallback = new ChatResponse();
            fallback.setReply("🤖 网络连接失败，请稍后重试。");
            return Result.success(fallback);
        }
    }
}
