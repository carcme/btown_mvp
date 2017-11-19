package me.carc.btown.data.all4squ;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by bamptonm on 21/08/2017.
 */

public interface FourSquareApi {

    String VERSION = "20171101";
    String ENDPOINT = "https://api.foursquare.com/v2/";

    @GET("users/{user_list_id}/lists")
    Call<FourSquResult> getBTownLists(@Path("user_list_id") String user_list_id);

    @GET("lists/{list_id}")
    Call<FourSquResult> getListDetails(@Path("list_id") String list_id);


    @GET("venues/{venue_id}")
    Call<FourSquResult> getVenueDetails(@Path("venue_id") String venue_id);


    @GET("venues/{venue_id}/photos")
    Call<FourSquResult> getVenueMorePhotos(@Path("venue_id") String venue_id,
                                           @Query("limit") int limit,
                                           @Query("offset") int offset);

    @GET("venues/{venue_id}/menu")
    Call<FourSquResult> getVenueMenu(@Path("venue_id") String venue_id);


    @GET("venues/{venue_id}/tips")
    Call<FourSquResult> getVenueTips(@Path("venue_id") String venue_id,
                                     @Query("sort") String sort,
                                     @Query("limit") int limit,
                                     @Query("offset") int offset);


    @GET("venues/search")
    Call<FourSquResult> searchArea(@Query("ll") String latLon,
                                   @Query("intent") String intent,
                                   @Query("limit") int limit,
                                   @Query("radius") int radius);


    @GET("venues/explore")
    Call<FourSquResult> explore(@Query("ll") String latLon,
                                @Query("limit") int limit,
                                @Query("radius") int radius);


}

