package site.milanchen.chat;

import site.milanchen.chat.constant.ChatRoleEnum;
import site.milanchen.chat.service.OpenAIService;
import site.milanchen.chat.vo.ChatCompletionRequest;
import site.milanchen.chat.vo.ChatCompletionResponse;
import site.milanchen.chat.dto.Message;
import site.milanchen.chat.vo.EmbeddingRequest;
import site.milanchen.chat.vo.EmbeddingResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author milan
 * @description
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class OpenAIServiceTest {

    @Autowired
    private OpenAIService openAIService;

    @Test
    public void embeddingsTest() {
        EmbeddingRequest embeddingRequest = EmbeddingRequest
                .builder()
                .input(Arrays.asList("123"))
                .build();
        EmbeddingResponse embeddingResponse = openAIService.embeddings(embeddingRequest);
        log.info("Test Result：{}", embeddingResponse);
    }

    @Test
    public void chatCompletionsTest() {
        ChatCompletionRequest chatCompletion = ChatCompletionRequest
                .builder()
                .messages(Collections.singletonList(Message.builder().role(ChatRoleEnum.USER.getCode()).content("讲一下二战历史").build()))
                .model(ChatCompletionRequest.Model.GPT_3_5_TURBO.getCode())
                .build();
        ChatCompletionResponse chatCompletionResponse = openAIService.chatCompletions(chatCompletion);
        chatCompletionResponse.getChoices().forEach(e -> {
            log.info("Test Result：{}", e.getMessage());
        });
    }

}
