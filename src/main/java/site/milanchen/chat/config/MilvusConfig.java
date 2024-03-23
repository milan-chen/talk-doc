package site.milanchen.chat.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author milan
 * @description
 */
@Configuration
public class MilvusConfig {

    @Value("${milvus.url}")
    private String url;
    @Value("${milvus.token}")
    private String token;

    @Bean
    public MilvusServiceClient milvusServiceClient() {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withUri(url)
                .withToken(token)
                .build();
        return new MilvusServiceClient(connectParam);
    }

}
