package site.milanchen.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.milanchen.chat.constant.ChatModeEnum;
import site.milanchen.chat.vo.ChatCompletionRequest;
import site.milanchen.chat.vo.EmbeddingRequest;
import site.milanchen.chat.vo.EmbeddingResponse;
import site.milanchen.chat.component.OpenAIEventSourceListener;
import site.milanchen.chat.service.OpenAIService;
import site.milanchen.chat.service.MilvusService;
import site.milanchen.chat.component.MilvusSchema;
import lombok.extern.slf4j.Slf4j;
import site.milanchen.chat.utils.UserSessionUtil;
import site.milanchen.chat.vo.MilvusSearchRequest;
import site.milanchen.chat.vo.MilvusSearchResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author milan
 * @description
 */
@RestController
@CrossOrigin
@Slf4j
public class ChatController {

    @Autowired
    private OpenAIService openAIService;
    @Autowired
    private MilvusService milvusService;

    @GetMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestParam String chatMode,
                                 @RequestParam String sessionId,
                                 @RequestParam String question) {
        log.info("#chatStream# request, {}, {}, {}", chatMode, sessionId, question);
        if (ChatModeEnum.FOCUS.getCode().equals(chatMode) && UserSessionUtil.existUploadRecord(sessionId)) {
            log.info("#chatStream# into milvus...");
            milvusService.loadCollection(MilvusSchema.COLLECTION_NAME);
            EmbeddingRequest embeddingRequest = EmbeddingRequest
                    .builder()
                    .input(Arrays.asList(question))
                    .build();
            EmbeddingResponse embeddings = openAIService.embeddings(embeddingRequest);
            List<Float> vectors = embeddings.getData().get(0).getEmbedding();
            MilvusSearchRequest milvusSearchRequest = MilvusSearchRequest.builder()
                    .collectionName(MilvusSchema.COLLECTION_NAME)
                    .queryVectors(Collections.singletonList(vectors))
                    .expr(MilvusSchema.Field.DOC_ID + " == \"" + sessionId + "\"")
                    .build();
            List<List<MilvusSearchResponse>> lists = milvusService.searchTopKSimilarity(milvusSearchRequest);
            StringBuffer buffer = new StringBuffer();
            lists.forEach(searchResultVos -> {
                searchResultVos.forEach(milvusSearchResponse -> {
                    buffer.append(milvusSearchResponse.getContent());
                });
            });
            String content = buffer.toString();
            question = "### " + content + " ### " + question;
        }
        log.info("#chatStream# request llm prompt: {}", question);
        SseEmitter sseEmitter = new SseEmitter();
        OpenAIEventSourceListener openAIEventSourceListener = new OpenAIEventSourceListener(sessionId, question, sseEmitter);
        ChatCompletionRequest chatCompletion = ChatCompletionRequest
                .builder()
                .stream(true)
                .sessionId(sessionId)
                .question(question)
                .build();
        openAIService.streamChatCompletions(chatCompletion, openAIEventSourceListener);
        return sseEmitter;
    }

    @GetMapping(value = "/reset-chat")
    public void resetChat(@RequestParam String sessionId) {
        log.info("#resetChat# request, {}", sessionId);
        UserSessionUtil.clearHistory(sessionId);
        milvusService.deleteEntity(MilvusSchema.COLLECTION_NAME, MilvusSchema.Field.DOC_ID + " == \"" + sessionId + "\"");
    }

}
