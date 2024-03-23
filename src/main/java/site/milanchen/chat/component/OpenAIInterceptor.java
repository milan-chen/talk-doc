package site.milanchen.chat.component;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author milan
 * @description
 */
public class OpenAIInterceptor implements Interceptor {

    private String apiKey;

    public OpenAIInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .header("Authorization", "Bearer " + apiKey)
                .build();
        return chain.proceed(request);
    }

}
