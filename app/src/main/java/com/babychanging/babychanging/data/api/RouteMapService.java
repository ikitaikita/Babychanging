package com.babychanging.babychanging.data.api;

import com.babychanging.babychanging.data.model.BabyCList;
import com.babychanging.babychanging.data.model.RouteList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by vik on 26/10/2017.
 */

public interface RouteMapService {

    @GET( "json" )
    Call<RouteList> getRouteList(@Query("origin") String origin,
                                 @Query("destination") String destination,
                                 @Query("sensor") String sensor,
                                 @Query("language") String language,
                                 @Query("units") String units);
}
