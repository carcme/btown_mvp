package me.carc.btown.data.route;

import me.carc.btown.data.model.RouteResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * graphhopper.com Directions API
 * Created by bamptonm on 21/08/2017.
 */

public interface RouteApi {

    String ENDPOINT = "https://graphhopper.com";

    @GET("/api/1/route?points_encoded=true&")
    Call<RouteResult> route(@Query("key") String apiKey,
                            @Query("point") String fromLatlon,
                            @Query("point") String toLatlon,
                            @Query("locale") String language,
                            @Query("vehicle") String thumbsize);
}
/*
https://graphhopper.com/api/1/route?key=2ec5681b-b1ab-47d5-a52e-113457d7a642&point=52.5232111,13.2983311&point=52.52298101104302,13.30170750617981&elevation=false&locale=en&vehicle=foot
 */