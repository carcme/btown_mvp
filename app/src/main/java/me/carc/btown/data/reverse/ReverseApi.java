package me.carc.btown.data.reverse;

import me.carc.btown.data.results.ReverseResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by bamptonm on 21/08/2017.
 */

public interface ReverseApi {

    String ENDPOINT = "http://nominatim.openstreetmap.org";

    @GET("/reverse?format=json&osm_type=N&addressdetails=1&extratags=1&namedetails=1&polygon_svg=1")
    @Headers("User-Agent: B-Town")
    Call<ReverseResult> reverseExtra(@Query("accept-language") String lang,
                                 @Query("lat") String lat,
                                 @Query("lon") String lon);


    //  EG: http://nominatim.openstreetmap.org/reverse?format=json&&lat=52.5206056&lon=13.2985634
    @GET("/reverse?format=json")
    @Headers("User-Agent: B-Town")
    Call<ReverseResult> reverse(@Query("lat") double lat, @Query("lon") double lon);
}

