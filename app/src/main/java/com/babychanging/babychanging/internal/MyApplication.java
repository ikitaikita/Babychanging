package com.babychanging.babychanging.internal;

import android.app.Application;


import com.babychanging.babychanging.model.BChanging;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by vik on 27/07/2017.
 */

public class MyApplication extends Application {

    public ArrayList<BChanging> getL_bchangings() {
        return l_bchangings;
    }

    public void setL_bchangings(ArrayList<BChanging> l_bchangings) {
        this.l_bchangings = l_bchangings;
    }

    public ArrayList<BChanging> getL_favourites() {
        return l_favourites;
    }

    public void setL_favourites(ArrayList<BChanging> l_favourites) {
        this.l_favourites = l_favourites;
    }

    private ArrayList<BChanging> l_bchangings = new ArrayList<BChanging>();
    private ArrayList<BChanging> l_favourites = new ArrayList<BChanging>();

    private LatLng startpoint = null;




    public void setStartpoint (LatLng start)
    {
        startpoint = start;

    }
    public LatLng getStartpoint ()
    {
        return startpoint;
    }




}
