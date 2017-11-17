package com.babychanging.babychanging;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import timber.log.Timber;

/**
 * Created by vik on 23/10/2017.
 */

public class BabyChangingApplication extends Application {
    private static BabyChangingApplication instance;

    @Override
    public void onCreate()
    {
        super.onCreate();

        instance = this;

        if (BuildConfig.DEBUG)
        {
            Timber.plant(new Timber.DebugTree());
        }

        Timber.i("Creating our Application");
    }

    public static BabyChangingApplication getInstance ()
    {
        return instance;
    }

    public static boolean hasNetwork ()
    {
        return instance.checkIfHasNetwork();
    }

    public boolean checkIfHasNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}
