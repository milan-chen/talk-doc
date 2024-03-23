package site.milanchen.chat.service;

import site.milanchen.chat.vo.ChatCompletionRequest;
import site.milanchen.chat.vo.ChatCompletionResponse;
import site.milanchen.chat.vo.EmbeddingRequest;
import site.milanchen.chat.vo.EmbeddingResponse;
import okhttp3.sse.EventSourceListener;

/**
 * @author milan
 * @description
 */
public interface OpenAIService {

    EmbeddingResponse embeddings(EmbeddingRequest embeddingRequest);

    ChatCompletionResponse chatCompletions(ChatCompletionRequest chatCompletionRequest);

    void streamChatCompletions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener);

}
