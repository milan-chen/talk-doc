package site.milanchen.chat.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author milan
 * @description
 */
@Data
@Builder
public class MilvusSearchResponse {

    private Long id;
    private Float score;
    private String content;

}
