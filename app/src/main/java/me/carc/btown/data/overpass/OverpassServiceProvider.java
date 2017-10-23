package me.carc.btown.data.overpass;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OverpassServiceProvider {

    private static OverpassApi service;

    public static OverpassApi get() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    private static OverpassApi createService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OverpassApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(OverpassApi.class);
    }
}