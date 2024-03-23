package site.milanchen.chat.service;

import site.milanchen.chat.vo.ChatCompletionRequest;
import site.milanchen.chat.vo.ChatCompletionResponse;
import site.milanchen.chat.vo.EmbeddingRequest;
import site.milanchen.chat.vo.EmbeddingResponse;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author milan
 * @description
 */
public interface OpenAIClient {

    String EMBEDDINGS_API = "v1/embeddings";

    String CHAT_COMPLETIONS_API = "v1/chat/completions";

    @POST(EMBEDDINGS_API)
    Single<EmbeddingResponse> embeddings(@Body EmbeddingRequest embeddingRequest);

    @POST(CHAT_COMPLETIONS_API)
    Single<ChatCompletionResponse> chatCompletions(@Body ChatCompletionRequest chatCompletionRequest);

}
