package site.milanchen.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author milan
 * @description
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ChatChoice implements Serializable {

    private long index;
    /** stream = true, return type is delta */
    @JsonProperty("delta")
    private Message delta;
    /** stream = false, return type is message */
    @JsonProperty("message")
    private Message message;
    @JsonProperty("finish_reason")
    private String finishReason;

}
