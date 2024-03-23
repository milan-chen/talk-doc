package site.milanchen.chat.vo;

import lombok.Data;
import site.milanchen.chat.dto.Item;
import site.milanchen.chat.dto.Usage;

import java.io.Serializable;
import java.util.List;

/**
 * @author milan
 * @description
 */
@Data
public class EmbeddingResponse implements Serializable {

    private String object;
    private List<Item> data;
    private String model;
    private Usage usage;

}
