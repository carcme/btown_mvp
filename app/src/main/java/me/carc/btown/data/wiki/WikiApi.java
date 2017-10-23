package me.carc.btown.data.wiki;

import android.support.annotation.NonNull;

import me.carc.btown.common.C;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by bamptonm on 21/08/2017.
 */

public interface WikiApi {

    String ENDPOINT = "https://" + C.USER_LANGUAGE + ".wikipedia.org";
    String ENDPOINT_EN = "https://en.wikipedia.org";
    String ENDPOINT_DE = "https://de.wikipedia.org";

    @GET("w/api.php?format=json&action=query&formatversion=2&redirects=1&prop=info|extracts|coordinates|pageimages|pageterms"
            + "&colimit=20&piprop=thumbnail&pilicense=any&wbptterms=description&exintro=1&inprop=url&&explaintext=1"
            + "&generator=geosearch&ggslimit=20&continue=")
    Call<WikiQueryResponse> requestNearBy(@NonNull @Query("ggscoord") String coord,
                                          @Query("ggsradius") double radius,
                                          @Query("pithumbsize") int thumbsize);


// By ID example
//    https://en.wikipedia.org/w/api.php?action=query&prop=info&pageids=692164&inprop=url

/* EXAMPLE
https://en.wikipedia.org/w/api.php?format=json&action=query&redirects=1&generator=geosearch&prop=extracts|coordinates|pageterms|pageimages&piprop=thumbnail&wbptterms=description&ggscoord=52.52322580612451|13.298317193984985&coprop=type|dim|globe&exlimit=20&pilimit=50&explaintext=1&ggsradius=10000&pithumbsize=960&codistancefrompoint=52.52322580612451|13.298317193984985&exintro=1&ggslimit=20&colimit=20
*/
}
