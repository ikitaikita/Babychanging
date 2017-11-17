package com.babychanging.babychanging.ui.detailbabyc;

import com.babychanging.babychanging.data.api.RouteMapService;
import com.babychanging.babychanging.data.model.BabyC;
import com.babychanging.babychanging.data.model.BabyCList;
import com.babychanging.babychanging.data.model.RouteList;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by vik on 25/10/2017.
 */

public class DetailBChangingPresenter {

    private static String SENSOR = "true";
    private static String LANGUAGE = "en";
    private static String UNITS = "metrics";

    DetailBChangingView view;
    RouteMapService routeMapService;



    private BabyC babyC;



    private double mLat,mLongi;
    private String origin,destination,sensor,language,units;

    public DetailBChangingPresenter (DetailBChangingView detailview, RouteMapService routeMapService)
    {
        this.view = detailview;
        this.routeMapService = routeMapService;

    }

    void clickSmallPic(){
        view.showBigPic();
    }
    void clickClosePic(){
        view.closeBigPic();
    }
    void shareButtonClick(){
        view.shareBabyC();
    }

    public void loadRouteData (String origin, String destination)
    {


        routeMapService.getRouteList(origin,destination,SENSOR,LANGUAGE,UNITS).enqueue( new Callback<RouteList>()
        {
            @Override
            public void onResponse (Call<RouteList> call, Response<RouteList> response)
            {
                if ( response.isSuccessful() )
                {
                    //listplacesView.hideProgress();
                    view.showDurationAndDistance(response.body().getRoutes());

                    //view.hideProgress();
                    Timber.i( "Books data was loaded from API." );
                }
            }

            @Override
            public void onFailure (Call<RouteList> call, Throwable t)
            {
                //listplacesView.hideProgress();
                view.showMessage("Error message.Unable to load the Rotes from RouteMapService ");
                Timber.e( t, "Unable to load the books data from API." );
            }
        } );
    }


    public BabyC getBabyC() {return babyC;}

    public void setBabyC(BabyC babyC) {this.babyC = babyC;}

    public double getmLat() {return mLat; }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLongi() {
        return mLongi;
    }

    public void setmLongi(double mLongi) {
        this.mLongi = mLongi;
    }


}
