package me.carc.btown.data.overpass;


import android.support.annotation.NonNull;

import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.data.results.OverpassQueryResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OverpassServiceProvider {
    public interface OverpassCallback {
        void onSucess(OverpassQueryResult result);
        void onFailure(@NonNull Call call, @NonNull Throwable t);
    }

    private static final String TAG = C.DEBUG + Commons.getTag();

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

    public Call<OverpassQueryResult> createAndRunService(String query, final OverpassCallback callback) {

        get();

        Call<OverpassQueryResult> call = service.interpreter(query);
        call.enqueue(new Callback<OverpassQueryResult>() {

            @Override
            @SuppressWarnings({"ConstantConditions"})
            public void onResponse(@NonNull Call<OverpassQueryResult> call, @NonNull Response<OverpassQueryResult> response) {
                assert response.body() != null;
                if (response.isSuccessful() && (Commons.isNotNull(response.body()) || response.body().elements.size() > 0)) {
                    callback.onSucess(response.body() == null ? null : response.body());
                } else
                    callback.onSucess(null);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                callback.onFailure(call, t);
            }
        });

        return call;
    }
}