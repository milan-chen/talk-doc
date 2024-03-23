package site.milanchen.chat.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import site.milanchen.chat.dto.Message;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author milan
 * @description
 */
@Data
@Builder
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionRequest implements Serializable {

    /**
     * Default Model
     */
    @Builder.Default
    private String model = Model.GPT_3_5_TURBO.getCode();
    /**
     * Message
     */
    private List<Message> messages;

    @Builder.Default
    private double temperature = 0.2;

    @Builder.Default
    @JsonProperty("top_p")
    private Double topP = 1d;

    @Builder.Default
    private Integer n = 1;

    private boolean stream = false;

    private List<String> stop;

    @Builder.Default
    @JsonProperty("max_tokens")
    private Integer maxTokens = 4096;

    @Builder.Default
    @JsonProperty("frequency_penalty")
    private double frequencyPenalty = 0;

    @Builder.Default
    @JsonProperty("presence_penalty")
    private double presencePenalty = 0;

    @JsonProperty("logit_bias")
    private Map logitBias;

    private String user;

    private String sessionId;

    private String question;

    @Getter
    @AllArgsConstructor
    public enum Model {
        /**
         * gpt-3.5-turbo
         */
        GPT_3_5_TURBO("gpt-3.5-turbo"),
        /**
         * GPT4.0
         */
        GPT_4("gpt-4"),
        /**
         * GPT4.0 large context
         */
        GPT_4_32K("gpt-4-32k"),
        ;
        private String code;
    }

}
