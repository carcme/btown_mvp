package me.carc.btown.data.wiki;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WikiServiceProvider {

    private static WikiApi service;

    public static WikiApi get() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    private static WikiApi createService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WikiApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(WikiApi.class);
    }
}