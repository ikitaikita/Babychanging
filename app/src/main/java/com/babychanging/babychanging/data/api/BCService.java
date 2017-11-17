package com.babychanging.babychanging.data.api;

import com.babychanging.babychanging.data.model.BabyC;
import com.babychanging.babychanging.data.model.BabyCList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Annyce Davis
 */
public interface BCService
{
    @GET( "datos.class.php" )
    Call<BabyCList> getBCList(@Query("tipo") String tipo);

    @POST( "datos.class.php" )
    Call<BabyC> saveBabyC (@Body BabyC babyC ,@Query("tipo") String tipo);


    // next versions
    @GET( "books/{id}" )
    Call<BabyC> getBabyC (@Path("id") Long id);

    @DELETE( "books/{id}")
    Call<Void> deleteBabyC (@Path("id") Long id);
}
