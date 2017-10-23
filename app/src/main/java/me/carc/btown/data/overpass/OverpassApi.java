package me.carc.btown.data.overpass;

import me.carc.btown.data.model.OverpassQueryResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by bamptonm on 21/08/2017.
 */

public interface OverpassApi {

    String ENDPOINT = "http://overpass-api.de";

    @GET("/api/interpreter")
    Call<OverpassQueryResult> interpreter(@Query("data") String data);

/* EXAMPLE
http://overpass-api.de/api/interpreter?data=[out:json][timeout:10][bbox:52.5191979376237,13.2944655418396,52.527227196315074,13.30219030380249];%20(node[amenity=ice_cream][name];%20node[amenity=food_court][name];%20node[amenity=biergarten][name];%20node[amenity=bbq][name];%20node[amenity=pub][name];%20node[amenity=restaurant][name];%20node[amenity=fast_food][name];%20node[amenity=cafe][name];%20node[amenity=bar][name];%3C;);%20out%20body%20center%20qt%20100;
*/
}

