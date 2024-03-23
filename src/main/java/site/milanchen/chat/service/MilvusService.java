package site.milanchen.chat.service;

import site.milanchen.chat.vo.ResultResponse;
import site.milanchen.chat.vo.MilvusSearchRequest;
import site.milanchen.chat.vo.MilvusSearchResponse;
import io.milvus.param.dml.InsertParam;

import java.util.List;

/**
 * @author milan
 * @description
 */
public interface MilvusService {

    void loadCollection(String collectionName);

    void releaseCollection(String collectionName);

    void loadPartitions(String collectionName, List<String> partitionsName);

    void releasePartitions(String collectionName, List<String> partitionsName);

    boolean isExitCollection(String collectionName);

    boolean createCollection(String collectionName);

    boolean dropCollection(String collectionName);

    boolean deleteEntity(String collectionName, String expr);

    void createPartition(String collectionName, String partitionName);

    boolean createIndex(String collectionName);

    ResultResponse insert(String collectionName, List<InsertParam.Field> fields);

    List<List<MilvusSearchResponse>> searchTopKSimilarity(MilvusSearchRequest contentParamVo);

}
