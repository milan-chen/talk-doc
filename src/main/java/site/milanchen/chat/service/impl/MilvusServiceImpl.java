package site.milanchen.chat.service.impl;

import io.milvus.param.dml.DeleteParam;
import org.springframework.beans.factory.annotation.Autowired;
import site.milanchen.chat.component.MilvusSchema;
import site.milanchen.chat.service.MilvusService;
import site.milanchen.chat.vo.ResultResponse;
import site.milanchen.chat.vo.MilvusSearchRequest;
import site.milanchen.chat.vo.MilvusSearchResponse;
import io.milvus.client.MilvusClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.GetIndexBuildProgressResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.GetIndexBuildProgressParam;
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.param.partition.LoadPartitionsParam;
import io.milvus.param.partition.ReleasePartitionsParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author milan
 * @description
 */
@Slf4j
@Service
public class MilvusServiceImpl implements MilvusService {

    @Autowired
    private MilvusClient milvusClient;

    @Override
    public void loadCollection(String collectionName) {
        LoadCollectionParam loadCollectionParam = LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        R<RpcStatus> response = milvusClient.loadCollection(loadCollectionParam);
        log.info("#loadCollection# {}, {}", collectionName, response.getData().getMsg());
    }

    @Override
    public void releaseCollection(String collectionName) {
        ReleaseCollectionParam param = ReleaseCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        R<RpcStatus> response = milvusClient.releaseCollection(param);
        log.info("#releaseCollection# {}, {}", collectionName, response.getData().getMsg());
    }

    @Override
    public void loadPartitions(String collectionName, List<String> partitionsName) {
        LoadPartitionsParam build = LoadPartitionsParam.newBuilder()
                .withCollectionName(collectionName)
                .withPartitionNames(partitionsName)
                .build();
        R<RpcStatus> rpcStatusR = milvusClient.loadPartitions(build);
        log.info("#loadPartitions# {}, {}", partitionsName, rpcStatusR.getData().getMsg());
    }

    @Override
    public void releasePartitions(String collectionName, List<String> partitionsName) {
        ReleasePartitionsParam build = ReleasePartitionsParam.newBuilder()
                .withCollectionName(collectionName)
                .withPartitionNames(partitionsName)
                .build();
        R<RpcStatus> rpcStatusR = milvusClient.releasePartitions(build);
        log.info("#releasePartitions# {}, {}", collectionName, rpcStatusR.getData().getMsg());
    }

    @Override
    public boolean isExitCollection(String collectionName) {
        HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        R<Boolean> response = milvusClient.hasCollection(hasCollectionParam);
        Boolean isExists = response.getData();
        log.info("#isExitCollection# {}, {}", collectionName, isExists);
        return isExists;
    }

    @Override
    public boolean createCollection(String collectionName) {
        FieldType fieldType1 = FieldType.newBuilder()
                .withName(MilvusSchema.Field.ID)
                .withDescription("primary key")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();
        FieldType fieldType2 = FieldType.newBuilder()
                .withName(MilvusSchema.Field.DOC_ID)
                .withDataType(DataType.VarChar)
                .withMaxLength(MilvusSchema.DOC_ID_MAX_LENGTH)
                .build();
        FieldType fieldType3 = FieldType.newBuilder()
                .withName(MilvusSchema.Field.CONTENT)
                .withDataType(DataType.VarChar)
                .withMaxLength(MilvusSchema.CONTENT_MAX_LENGTH)
                .build();
        FieldType fieldType4 = FieldType.newBuilder()
                .withName(MilvusSchema.Field.CONTENT_VECTOR)
                .withDataType(DataType.FloatVector)
                .withDimension(MilvusSchema.FEATURE_DIM)
                .build();
        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withShardsNum(MilvusSchema.SHARDS_NUM)
                .addFieldType(fieldType1)
                .addFieldType(fieldType2)
                .addFieldType(fieldType3)
                .addFieldType(fieldType4)
                .build();
        R<RpcStatus> response = milvusClient.createCollection(createCollectionReq);
        log.info("#createCollection# {}, {}", collectionName, response.getData().getMsg());
        return response.getData().getMsg().equals("Success");
    }

    @Override
    public boolean dropCollection(String collectionName) {
        DropCollectionParam book = DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        R<RpcStatus> response = milvusClient.dropCollection(book);
        return response.getData().getMsg().equals("Success");
    }

    @Override
    public boolean deleteEntity(String collectionName, String expr) {
        DeleteParam deleteParam = DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(expr)
                .build();
        R<MutationResult> response = milvusClient.delete(deleteParam);
        return response.getStatus().equals(0);
    }

    @Override
    public void createPartition(String collectionName, String partitionName) {
        CreatePartitionParam param = CreatePartitionParam.newBuilder()
                .withCollectionName(collectionName)
                .withPartitionName(partitionName)
                .build();
        R<RpcStatus> partition = milvusClient.createPartition(param);
        String msg = partition.getData().getMsg();
        log.info("#createPartition# {}, {}, {}", partition, collectionName, msg);
    }

    @Override
    public boolean createIndex(String collectionName) {
        R<RpcStatus> response = milvusClient.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName(MilvusSchema.Field.CONTENT_VECTOR)
                .withIndexName(MilvusSchema.CONTENT_VECTOR_INDEX)
                .withIndexType(IndexType.AUTOINDEX)
                .withMetricType(MetricType.L2)
                .build());
        GetIndexBuildProgressParam build = GetIndexBuildProgressParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        R<GetIndexBuildProgressResponse> indexResp = milvusClient.getIndexBuildProgress(build);
        log.info("#createIndex# {}, {}", response.toString(), indexResp.getStatus());
        return response.getData().getMsg().equals("Success");
    }

    @Override
    public ResultResponse insert(String collectionName, List<InsertParam.Field> fields) {
        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();
        R<MutationResult> mutationResultR = milvusClient.insert(insertParam);
        log.info("#insert# Flushing...");
        milvusClient.flush(FlushParam.newBuilder()
                .withCollectionNames(Collections.singletonList(collectionName))
                .withSyncFlush(true)
                .withSyncFlushWaitingInterval(50L)
                .withSyncFlushWaitingTimeout(30L)
                .build());
        if (mutationResultR.getStatus() == 0) {
            long insertCnt = mutationResultR.getData().getInsertCnt();
            log.info("#insert# success! {} ", insertCnt);
            return ResultResponse.success(insertCnt);
        }
        log.error("#insert# failed!");
        return ResultResponse.failure("文本向量化失败！");
    }

    @Override
    public List<List<MilvusSearchResponse>> searchTopKSimilarity(MilvusSearchRequest milvusSearchRequest) {
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(milvusSearchRequest.getCollectionName())
                .withMetricType(MetricType.L2)
                .withOutFields(milvusSearchRequest.getOutputFields())
                .withTopK(milvusSearchRequest.getTopK())
                .withVectors(milvusSearchRequest.getQueryVectors())
                .withVectorFieldName(MilvusSchema.Field.CONTENT_VECTOR)
                .withParams(milvusSearchRequest.getParams())
                .withExpr(milvusSearchRequest.getExpr())
                .build();

        R<SearchResults> respSearch = milvusClient.search(searchParam);
        if (respSearch.getData() == null) {
            return null;
        }
        SearchResultsWrapper wrapper = new SearchResultsWrapper(respSearch.getData().getResults());
        List<List<MilvusSearchResponse>> result = new ArrayList<>();
        for (int i = 0; i < milvusSearchRequest.getQueryVectors().size(); ++i) {
            List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(i);
            List<QueryResultsWrapper.RowRecord> rowRecords = wrapper.getRowRecords();
            List<MilvusSearchResponse> list = new ArrayList<>();
            for (int j = 0; j < scores.size(); ++j) {
                SearchResultsWrapper.IDScore score = scores.get(j);
                QueryResultsWrapper.RowRecord rowRecord = rowRecords.get(j);
                long longID = score.getLongID();
                float distance = score.getScore();
                String content = (String) rowRecord.get(milvusSearchRequest.getOutputFields().get(0));
                log.info("#searchTopKSimilarity# Top " + j + " ID:" + longID + " Distance:" + distance);
                log.info("#searchTopKSimilarity# Content: " + content);
                list.add(MilvusSearchResponse.builder().id(longID).score(distance).content(content).build());
            }
            result.add(list);
        }
        return result;
    }
}
