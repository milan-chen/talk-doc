package site.milanchen.chat.config;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import site.milanchen.chat.service.OpenAIClient;
import site.milanchen.chat.component.OpenAIInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * @author milan
 * @description
 */
@Configuration
public class OpenAIConfig {

    @Value("${openai.host}")
    private String host;
    @Value("${openai.key}")
    private String key;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient
                .Builder()
                .addInterceptor(new OpenAIInterceptor(key))
                .connectTimeout(450, TimeUnit.SECONDS)
                .writeTimeout(450, TimeUnit.SECONDS)
                .readTimeout(450, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public OpenAIClient openAIClient() {
        return new Retrofit.Builder()
                .baseUrl(host)
                .client(this.okHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(OpenAIClient.class);
    }

}
