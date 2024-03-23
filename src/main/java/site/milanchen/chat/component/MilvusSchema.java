package site.milanchen.chat.component;

/**
 * @author milan
 * @description
 */
public class MilvusSchema {

    /**
     * 集合名称
     */
    public static final String COLLECTION_NAME = "talk_doc";
    /**
     * 分区名称
     */
    public static final String PARTITION_NAME = "content_partion";
    /**
     * 索引名称
     */
    public static final String CONTENT_VECTOR_INDEX = "content_vector_index";

    /**
     * 分片数量
     */
    public static final Integer SHARDS_NUM = 1;
    /**
     * 分区数量
     */
    public static final Integer PARTITION_NUM = 1;

    /**
     * 分区前缀
     */
    public static final String PARTITION_PREFIX = "shards_";


    public static final Integer DOC_ID_MAX_LENGTH = 256;

    /**
     * 文本内容最大长度
     */
    public static final Integer CONTENT_MAX_LENGTH = 10240;

    /**
     * 向量值长度
     */
    public static final Integer FEATURE_DIM = 1536;

    /**
     * 字段
     */
    public static class Field {

        /**
         * 主键id
         */
        public static final String ID = "id";
        /**
         * 文档标识
         */
        public static final String DOC_ID = "doc_id";
        /**
         * 文本内容
         */
        public static final String CONTENT = "content";
        /**
         * 向量值
         */
        public static final String CONTENT_VECTOR = "content_vector";

    }

}
