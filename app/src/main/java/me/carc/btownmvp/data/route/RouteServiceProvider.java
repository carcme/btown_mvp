package me.carc.btownmvp.data.route;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RouteServiceProvider {

    private static RouteApi service;

    public static RouteApi get() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    private static RouteApi createService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RouteApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RouteApi.class);
    }
}