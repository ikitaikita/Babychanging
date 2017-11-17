package com.babychanging.babychanging.ui.upload;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.babychanging.babychanging.R;
import com.babychanging.babychanging.utils.Utils;

import timber.log.Timber;

public class UploadInteractorImpl implements UploadInteractor {

    private Context context;
    private Activity activity;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    LocationManager mLocationManager;
    //CustomLocationListener customLocationListener;
    OnCustomLocationListener listener;

    public UploadInteractorImpl(Context context, Activity activity){
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
        this.activity = activity;
        //customLocationListener = new CustomLocationListener();
    }

    @Override
    public void requestLocation(OnCustomLocationListener listener) {
        this.listener = listener;
        gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location net_loc = null, gps_loc = null;
        //Check, if we already have permission
        if ((ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            if (ActivityCompat
                    .shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                listener.showAlertDialog(context.getString(R.string.permission_location_explanation));

                // Display an AlertDialog with an explanation and a button to trigger the request.
                /*new android.app.AlertDialog.Builder(context)
                        .setMessage(context.getString(R.string.permission_location_explanation))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat
                                        .requestPermissions(activity, Utils.PERMISSIONS_LOCATION,
                                                Utils.REQUEST_LOCATION);
                            }
                        }).show();*/
            } else {
                ActivityCompat.requestPermissions(activity, Utils.PERMISSIONS_LOCATION,
                        Utils.REQUEST_LOCATION);
            }
        } else {
            //We already got the permissions, to proceed normally
            //Only proceed to start the App, if initialization is finished
            if (gps_enabled) {

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, customLocationListener);
                gps_loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (network_enabled)
            {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, customLocationListener);
                net_loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (gps_loc != null && net_loc != null) {

                if (gps_loc.getAccuracy() >= net_loc.getAccuracy())
                {
                    //Log.i(TAG, "chosen Location: "+ "GPS");
                    listener.setLocationDevice(gps_loc);
                    //mDeviceLocation = gps_loc;
                    /*startpoint = new LatLng(latitude,longitude);
                    application.setStartpoint(startpoint);*/

                }

                else
                {
                    //Log.i(TAG, "chosen Location: "+ "NETWORK");
                    listener.setLocationDevice(net_loc);
                    //mDeviceLocation = net_loc;
                    /*startpoint = new LatLng(latitude,longitude);
                    application.setStartpoint(startpoint);*/
                }


                // I used this just to get an idea (if both avail, its upto you which you want to take as I taken location with more accuracy)

            } else {

                if (gps_loc != null) {
                    //mDeviceLocation = net_loc;
                    listener.setLocationDevice(net_loc);
                } else if (net_loc != null) {
                    //mDeviceLocation = gps_loc;
                    listener.setLocationDevice(gps_loc);
                }
            }
            return;
        }
    }

    private LocationListener customLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Timber.i( "onLocationChanged1: " + location.toString());

            listener.setLocationDevice(location);
            //mDeviceLocation = argLocation;
            listener.setNewLatitudeOnDevice(location.getLatitude());
            //latitude = mDeviceLocation.getLatitude();
            //longitude = mDeviceLocation.getLongitude();
            listener.setNewLongitudeOnDevice(location.getLongitude());

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
  /*  private class CustomLocationListener implements LocationListener {

        OnCustomLocationListener listener;

        public void onLocationChanged(Location argLocation) {
            //Log.i("++++++++++","CustomLocationListener");
            listener.setLocationDevice(argLocation);
            //mDeviceLocation = argLocation;
            listener.setNewLatitudeOnDevice(argLocation.getLatitude());
            //latitude = mDeviceLocation.getLatitude();
            //longitude = mDeviceLocation.getLongitude();
            listener.setNewLongitudeOnDevice(argLocation.getLongitude());
           *//* startpoint = new LatLng(latitude,longitude);
            application.setStartpoint(startpoint);*//*



            //Location locAnte = distance_list[pos-1];


            mLocationManager.removeUpdates(this);
        }

        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {}
    }*/
}
