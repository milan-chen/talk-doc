package site.milanchen.chat;

import io.milvus.param.dml.InsertParam;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import site.milanchen.chat.vo.EmbeddingRequest;
import site.milanchen.chat.vo.EmbeddingResponse;
import site.milanchen.chat.dto.Item;
import site.milanchen.chat.service.OpenAIService;
import site.milanchen.chat.service.MilvusService;
import site.milanchen.chat.component.MilvusSchema;
import site.milanchen.chat.vo.MilvusSearchRequest;
import site.milanchen.chat.vo.MilvusSearchResponse;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author milan
 * @description
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class MilvusServiceTest {

    @Autowired
    private MilvusService milvusService;
    @Autowired
    private OpenAIService openAIService;

    private static final String COLLECTION_NAME = "talk_doc";

    @Test
    public void dropCollection() {
        boolean mediumArticles = milvusService.dropCollection(COLLECTION_NAME);
        Assertions.assertTrue(mediumArticles);
    }

    @Test
    public void createCollection() {
        boolean created = milvusService.createCollection(COLLECTION_NAME);
        boolean index = milvusService.createIndex(COLLECTION_NAME);
        Assertions.assertTrue(created);
        Assertions.assertTrue(index);
    }

    @Test
    public void insertVector() {
        List<String> paragraphs = Arrays.asList(
                "6月12日，日本东京电力公司开始对福岛第一核电站核污染水排海设备进行试运行。8月24日，日本不顾国际社会强烈反对，正式启动第一轮核污染水排海。中国海关总署发布公告表示，将从当日开始全面暂停进口原产地为日本的水产品。目前，日本已完成共计3轮核污染水排海，累计排放量超过2.3万吨。",
                "东京电力公司称，第四轮排海将于2024年2月下旬开始。今年6月，《环球时报》面向中国、韩国、日本、菲律宾、新西兰等11个国家发起民意调查，在1.1万余位受访者中，近九成受访者对核污染水排海表示担忧、恐惧和愤怒，超九成受访者支持本国采取措施干预日方排海。日方强推核污染水排海，无疑是在拿全球海洋环境和全人类健康当赌注。",
                "在这个问题上，国际社会可对日本无限期追责。中方外交部发言人表示，日方应当以严肃认真的态度回应国际社会的关切，以负责任的方式处置核污染水，全面配合建立有日本周边邻国等利益攸关方实质性参与、长期有效的国际监测安排，防止核污染水排海造成不可挽回的后果。");
        EmbeddingRequest embeddingRequest = EmbeddingRequest
                .builder()
                .input(paragraphs)
                .build();
        EmbeddingResponse embeddingResponse = openAIService.embeddings(embeddingRequest);
        List<List<Float>> vectors = embeddingResponse.getData().stream().map(Item::getEmbedding).collect(Collectors.toList());
        List<InsertParam.Field> fields = new ArrayList<>();
        String sessionId = String.valueOf(UUID.randomUUID());
        log.info("========sessionId========={}", sessionId); // 697644bf-2a70-4c71-b707-e869aa444246  c2aa684d-50fb-451f-990e-12cdd1670c63  84ab1e4e-f6f8-44f2-a63b-9ba592c17890
        fields.add(new InsertParam.Field(MilvusSchema.Field.DOC_ID, Collections.nCopies(paragraphs.size(), sessionId)));
        fields.add(new InsertParam.Field(MilvusSchema.Field.CONTENT, paragraphs));
        fields.add(new InsertParam.Field(MilvusSchema.Field.CONTENT_VECTOR, vectors));
        milvusService.insert(COLLECTION_NAME, fields);
    }

    @Test
    public void searchTest() {
        milvusService.loadCollection(COLLECTION_NAME);
        EmbeddingRequest embeddingRequest = EmbeddingRequest
                .builder()
                .input(Arrays.asList("已经完成了几轮核污水排海？"))
                .build();
        EmbeddingResponse embeddingResponse = openAIService.embeddings(embeddingRequest);
        List<Float> vectors = embeddingResponse.getData().get(0).getEmbedding();
        String docId = "c2aa684d-50fb-451f-990e-12cdd1670c63";
        MilvusSearchRequest milvusSearchRequest = MilvusSearchRequest.builder()
                .collectionName(COLLECTION_NAME)
                .queryVectors(Collections.singletonList(vectors))
                .topK(1)
                .expr(MilvusSchema.Field.DOC_ID + " == \"" + docId + "\"")
                .build();
        List<List<MilvusSearchResponse>> lists = milvusService.searchTopKSimilarity(milvusSearchRequest);
        lists.forEach(searchResponses -> {
            searchResponses.forEach(milvusSearchResponse -> {
                System.out.println(milvusSearchResponse.getContent());
            });
        });
    }

    @Test
    public void deleteEntityTest() {
//        String docId = "c2aa684d-50fb-451f-990e-12cdd1670c63";
//        boolean deleted = milvusService.deleteEntity(COLLECTION_NAME, MilvusSchema.Field.DOC_ID + " == \"" + docId + "\"");
//        Assertions.assertTrue(deleted);
    }

}