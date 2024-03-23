package site.milanchen.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import site.milanchen.chat.vo.ResultResponse;
import site.milanchen.chat.vo.EmbeddingRequest;
import site.milanchen.chat.vo.EmbeddingResponse;
import site.milanchen.chat.dto.Item;
import site.milanchen.chat.service.OpenAIService;
import site.milanchen.chat.service.MilvusService;
import site.milanchen.chat.utils.DocumentParseUtil;
import site.milanchen.chat.component.MilvusSchema;
import site.milanchen.chat.utils.IPUtil;
import site.milanchen.chat.utils.UserSessionUtil;
import io.milvus.param.dml.InsertParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author milan
 * @description
 */
@RestController
@CrossOrigin
@Slf4j
public class DocsController {

    @Autowired
    private OpenAIService openAIService;
    @Autowired
    private MilvusService milvusService;

    /**
     * 2小时内文档限制上传3次
     */
    private static final Integer DURATION_IP_UPLOAD_LIMIT_TIMES = 3;
    private static final long DURATION_IP_LIMIT_TIMES_SECOND = 2 * 60 * 60;

    @PostMapping("/upload")
    public ResultResponse upload(@RequestParam String sessionId, MultipartFile file, HttpServletRequest servletRequest) {
        log.info("#upload# request, {}, {}, {}", sessionId, file.getOriginalFilename(), (double) file.getSize() / (1024 * 1024));
        String ip = IPUtil.getClientIp(servletRequest);
        Integer ipUploadTimes = UserSessionUtil.getIpUploadTimes(ip);
        log.info("#upload# {}, {}, {}", ip, ipUploadTimes + 1, DURATION_IP_UPLOAD_LIMIT_TIMES);
        if (ipUploadTimes + 1 > DURATION_IP_UPLOAD_LIMIT_TIMES) {
            log.info("#upload# frequently...");
            return ResultResponse.failure("上传次数超过限制！");
        }
        try {
            List<String> paragraphs = DocumentParseUtil.parse(file.getInputStream());
            ResultResponse resultResponse = insertVector(sessionId, paragraphs);
            if (!resultResponse.isSuccess()) {
                throw new RuntimeException();
            }
            UserSessionUtil.addUploadRecord(sessionId);
            UserSessionUtil.addIpUploadTimes(ip, DURATION_IP_LIMIT_TIMES_SECOND);
            return resultResponse;
        } catch (Exception e) {
            log.error("#upload# error", e);
            return ResultResponse.failure("上传过程出现意外...");
        }
    }

    private ResultResponse insertVector(String sessionId, List<String> paragraphs) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest
                .builder()
                .input(paragraphs)
                .build();
        EmbeddingResponse embeddingResponse = openAIService.embeddings(embeddingRequest);
        List<List<Float>> vectors = embeddingResponse.getData().stream().map(Item::getEmbedding).collect(Collectors.toList());
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(MilvusSchema.Field.DOC_ID, Collections.nCopies(paragraphs.size(), sessionId)));
        fields.add(new InsertParam.Field(MilvusSchema.Field.CONTENT, paragraphs));
        fields.add(new InsertParam.Field(MilvusSchema.Field.CONTENT_VECTOR, vectors));
        return milvusService.insert(MilvusSchema.COLLECTION_NAME, fields);
    }

}
