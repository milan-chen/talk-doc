package site.milanchen.chat.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import site.milanchen.chat.dto.ChatChoice;
import site.milanchen.chat.dto.Usage;

import java.io.Serializable;
import java.util.List;

/**
 * @author milan
 * @description
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ChatCompletionResponse implements Serializable {

    /** ID */
    private String id;
    /** 对象 */
    private String object;
    /** 模型 */
    private String model;
    /** 对话 */
    private List<ChatChoice> choices;
    /** 创建 */
    private long created;
    /** 耗材 */
    private Usage usage;

}
