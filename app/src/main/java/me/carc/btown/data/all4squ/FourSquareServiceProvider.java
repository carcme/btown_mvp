package me.carc.btown.data.all4squ;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Locale;

import me.carc.btown.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FourSquareServiceProvider {

    private static FourSquareApi service;

    public static FourSquareApi get() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    private static FourSquareApi createService() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("client_id", BuildConfig.FOURSQ_ID)
                        .addQueryParameter("client_secret", BuildConfig.FOURSQ_SECRET)
                        .addQueryParameter("v", FourSquareApi.VERSION)
                        .build();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept-Language", Locale.getDefault().getLanguage())
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FourSquareApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(FourSquareApi.class);
    }
}