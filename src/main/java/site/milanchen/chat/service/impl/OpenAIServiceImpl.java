package site.milanchen.chat.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.sse.EventSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.milanchen.chat.constant.ChatRoleEnum;
import site.milanchen.chat.service.OpenAIClient;
import site.milanchen.chat.service.OpenAIService;
import site.milanchen.chat.vo.ChatCompletionRequest;
import site.milanchen.chat.vo.ChatCompletionResponse;
import site.milanchen.chat.dto.Message;
import site.milanchen.chat.vo.EmbeddingRequest;
import site.milanchen.chat.vo.EmbeddingResponse;
import okhttp3.Request;
import okhttp3.sse.EventSourceListener;
import site.milanchen.chat.utils.UserSessionUtil;

import java.util.List;

/**
 * @author milan
 * @description
 */
@Slf4j
@Service
public class OpenAIServiceImpl implements OpenAIService {

    /**
     * tokens和中文的转化比例
     */
    private static final float TOKEN_CONVERSION_RATE = 0.7f;
    /**
     * 最长tokens
     */
    private static final Integer MAX_TOKEN = 4096;
    /**
     * 最大中文长度
     */
    private static final Integer CHINESE_LENGTH = (int) (MAX_TOKEN / TOKEN_CONVERSION_RATE);

    @Value("${openai.host}")
    private String host;
    @Autowired
    private OpenAIClient openAIClient;
    @Autowired
    private OkHttpClient okHttpClient;

    @Override
    public EmbeddingResponse embeddings(EmbeddingRequest embeddingRequest) {
        return openAIClient.embeddings(embeddingRequest).blockingGet();
    }

    @Override
    public ChatCompletionResponse chatCompletions(ChatCompletionRequest chatCompletionRequest) {
        return openAIClient.chatCompletions(chatCompletionRequest).blockingGet();
    }

    @Override
    public void streamChatCompletions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) {
        if (!chatCompletionRequest.isStream()) {
            throw new RuntimeException("参数错误");
        }
        String sessionId = chatCompletionRequest.getSessionId();
        String question = chatCompletionRequest.getQuestion();
        int currentToken = (int) (question.length() / TOKEN_CONVERSION_RATE);
        if (currentToken >= MAX_TOKEN) {
            throw new RuntimeException("超出字数限制");
        }
        List<Message> history = UserSessionUtil.getHistory(sessionId, (int) (CHINESE_LENGTH - currentToken));
//        log.info("#streamChatCompletions# {}", history);
        history.add(Message.builder().role(ChatRoleEnum.USER.getCode()).content(question).build());
        chatCompletionRequest.setMessages(history);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(chatCompletionRequest));
        Request request = new Request.Builder()
                .url(host.concat(OpenAIClient.CHAT_COMPLETIONS_API))
                .post(requestBody)
                .build();
        EventSources.createFactory(okHttpClient).newEventSource(request, eventSourceListener);
    }

}
