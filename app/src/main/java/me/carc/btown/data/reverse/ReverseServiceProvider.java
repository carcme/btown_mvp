package me.carc.btown.data.reverse;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReverseServiceProvider {

    private static ReverseApi service;

    public static ReverseApi get() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    private static ReverseApi createService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ReverseApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ReverseApi.class);
    }
}