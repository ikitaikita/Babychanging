package com.babychanging.babychanging.internal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.babychanging.babychanging.model.BChanging;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by vik on 26/07/2017.
 */

public class Utils {
    public static String[]choices = {"Good","Regular","Bad"};
    public static final String GOOD="Good"; //para piscinas naturales y playas fluviales
    public static final String REGULAR="Regular"; //para playas nudistas
    public static final String BAD="Bad"; //para termas
    public static final String G="G"; //para pozas
    public static final String R = "R";
    public static final String B = "B";

    public static final int REQUEST_LOCATION = 0;
    public static String[] PERMISSIONS_LOCATION = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};




    public static void showAlert(Context context, String title, String message)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        if(!title.equals("")) alert.setTitle(title);
        alert.setMessage(message);
        alert.setCancelable(true);

        alert.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });


        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    public static String getCharFromState(String state)
    {
        String return_string ="";
        switch (state)
        {
            case GOOD:
                return_string=G;
                break;
            case REGULAR:
                return_string=R;
                break;
            case BAD:
                return_string=B;
                break;

            default:
                break;
        }
        return return_string;
    }

    public static String getStateFromChar(String character)
    {
        String return_string ="";
        switch (character)
        {
            case G:
                return_string=GOOD;

                break;
            case R:
                return_string=REGULAR;
                break;
            case B:
                return_string=BAD;
                break;

            default:
                break;
        }
        return return_string;
    }

    public static ArrayList<BChanging> orderListByDistance(ArrayList<BChanging> list_changings, double distance, LatLng startpoint)
    {
        Log.i("list_changings.size: " , String.valueOf(list_changings.size()));
        ArrayList<BChanging> new_list = new ArrayList<BChanging> ();
        //LatLng startpoint = new LatLng(latitude,longitude);
        Log.i("startpoint: " , startpoint.toString());
        LatLng endpoint;
        double newdistance;
        Iterator<BChanging> it1 = list_changings.iterator();
        while (it1.hasNext()){
            BChanging tmp = it1.next();
            endpoint = new LatLng(Double.parseDouble(tmp.getLatitude()),Double.parseDouble(tmp.getLongitude()));
            Log.i("startpoint: " , startpoint.toString());
            newdistance = Utils.calculationByDistanceKM(startpoint, endpoint);
            Log.i("newdistance: " , String.valueOf(newdistance));


            //Log.i(TAG, "orderListByDistance, distance: " +String.valueOf(newdistance));

            tmp.setDistance(Math.ceil(newdistance));
            new_list.add(tmp);
            Log.i("new item added: ", tmp.getId() + " " + String.valueOf(tmp.getDistance()));
        }
        Collections.sort(new_list, new Comparator<BChanging>() {
            @Override
            public int compare(BChanging d1, BChanging d2) {
                return d1.getDistance().compareTo(d2.getDistance());
            }
        });

        return new_list;


    }

    public static double calculationByDistanceKM(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius Earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
    public static Bitmap resizeImageToMax(Bitmap mBitmap, float newWidth, float newHeigth){
        //Redimensionamos
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeigth) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
    }

}
