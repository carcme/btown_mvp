package me.carc.btown_map.data.autocomplete;

import java.util.Map;

import me.carc.btown_map.data.model.AutoCompleteResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by bamptonm on 21/08/2017.
 */

public interface AutoCompleteApi {

    String ENDPOINT = "http://photon.komoot.de";

    @GET("/api/")
    Call<AutoCompleteResult> autoComplete(@QueryMap Map<String, String> filters);

/* EXAMPLE
http://photon.komoot.de/api/?q=Oscar&lon=13.298317193984985&lat=52.52322580612451
 */
}

