package me.carc.btown_map.data.autocomplete;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AutoCompleteServiceProvider {

    private static AutoCompleteApi service;

    public static AutoCompleteApi get() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    private static AutoCompleteApi createService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AutoCompleteApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AutoCompleteApi.class);
    }
}