package site.milanchen.chat.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author milan
 * @description
 */
@Data
public class Item implements Serializable {

    private String object;
    private List<Float> embedding;
    private Integer index;

}
