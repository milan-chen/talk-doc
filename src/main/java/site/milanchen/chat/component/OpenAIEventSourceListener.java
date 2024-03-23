package site.milanchen.chat.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.milanchen.chat.constant.ChatRoleEnum;
import site.milanchen.chat.vo.ChatCompletionResponse;
import site.milanchen.chat.dto.Message;
import site.milanchen.chat.utils.UserSessionUtil;

import java.io.IOException;
import java.util.Objects;

/**
 * @author milan
 * @description
 */
@Slf4j
public class OpenAIEventSourceListener extends EventSourceListener {

    private String sessionId;
    private String question;
    private SseEmitter sseEmitter;
    private StringBuilder stringBuilder;

    public OpenAIEventSourceListener(String sessionId, String question, SseEmitter sseEmitter) {
        this.sessionId = sessionId;
        this.question = question;
        this.sseEmitter = sseEmitter;
        this.stringBuilder = new StringBuilder();
    }

    @Override
    public void onOpen(EventSource eventSource, Response response) {
        sendSseEvent("[DONE]", "open", "[DONE]");
        log.info("EventSource opened");
    }

    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        if (data.equals("[DONE]")) {
            sendSseEvent("[DONE]", "close", "[DONE]");
            UserSessionUtil.addMessage(sessionId, new Message(ChatRoleEnum.USER.getCode(), question));
            UserSessionUtil.addMessage(sessionId, new Message(ChatRoleEnum.ASSISTANT.getCode(), stringBuilder.toString()));

            log.info("Event processing completed, {}", stringBuilder.toString().replace(" ", "").replace("\n", ""));
            return;
        }
        ChatCompletionResponse completionResponse = JSON.parseObject(data, ChatCompletionResponse.class);
        Message message = completionResponse.getChoices().get(0).getDelta();
        String content = StringUtils.isNotEmpty(message.getContent()) ? message.getContent() : "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", content);
        stringBuilder.append(content);
        Thread.sleep(30); // 加上可以让打字机效果更明显
        sendSseEvent(completionResponse.getId(), "message", jsonObject.toJSONString());
    }

    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        log.error("EventSource failure", t);

        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            String resp = body.string();
            if (resp.contains("tokens") && resp.contains("error")) {
                sendSseEvent("[DONE]", "close", resp.replace("\n", ""));
            } else {
                sendSseEvent("[DONE]", "error", "[DONE]");
            }
        } else {
            sendSseEvent("[DONE]", "error", "[DONE]");
        }
        eventSource.cancel();
    }

    private void sendSseEvent(String id, String name, String data) {
        try {
            sseEmitter.send(SseEmitter.event().id(id).name(name).data(data).reconnectTime(3000));
        } catch (IOException e) {
            log.error("Failed to send SSE event", e);
            throw new RuntimeException(e);
        }
    }

}