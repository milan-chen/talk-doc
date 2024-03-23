package site.milanchen.chat.vo;

import lombok.NonNull;
import site.milanchen.chat.component.MilvusSchema;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author milan
 * @description
 */
@Data
@Builder
public class MilvusSearchRequest {

    private String collectionName;
    private List<List<Float>> queryVectors;
    @NonNull
    private String expr;
    @Builder.Default
    private Integer topK = 5;
    @Builder.Default
    private List<String> outputFields = Arrays.asList(MilvusSchema.Field.CONTENT);
    @Builder.Default
    private String params = "{\"nprobe\":1024}";

}
