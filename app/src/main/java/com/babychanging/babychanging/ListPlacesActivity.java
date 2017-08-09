package com.babychanging.babychanging;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;


import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.babychanging.babychanging.internal.AccessInterface;
import com.babychanging.babychanging.internal.MyApplication;
import com.babychanging.babychanging.internal.Utils;
import com.babychanging.babychanging.model.BChanging;
import com.babychanging.babychanging.persistence.PersistenceSQL;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class ListPlacesActivity extends Activity {

    public static final String TAG = "ListPlacesActivity";

    private ListView lv_bchangings;
    private ArrayList<BChanging> list_changings = new ArrayList();
    private ArrayList<BChanging> list_changings_aux = new ArrayList();
    private ArrayList<BChanging> l_favourites= new ArrayList<BChanging>();
    private MyApplication application;

    private ProgressDialog pDialog;
    private Handler handler = new Handler(new ResultMessageCallback());
    public final int RESULT_GET_ERROR = -1;
    public final int RESULT_GET_OK = 1;
    private String menserror = "Error";

    //GPS

    private LatLng startpoint;
    CustomLocationListener customLocationListener = new CustomLocationListener();
    private Location m_DeviceLocation = null;
    private LocationManager mLocationManager;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private double latitude = 42.598726;
    private double longitude = -5.567096;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        application = (MyApplication) getApplicationContext();
        lv_bchangings = (ListView)findViewById(R.id.list);

        //CustomLocationListener customLocationListener = new CustomLocationListener();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Request Permissions for Android 6.0
        requestLocationPermission();


        if(m_DeviceLocation!=null)
        {

            Log.i(TAG, "tengo localizacion");

            latitude = m_DeviceLocation.getLatitude();
            longitude = m_DeviceLocation.getLongitude();

        }
        else
        {
            Log.i(TAG, "waiting location....");
        }
        Log.i(TAG, "latitude: "+ String.valueOf(latitude));
        Log.i(TAG, "longitude: "+  String.valueOf(longitude));
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        getListBChangings();

        lv_bchangings.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                int fixedpos = arg2;





                Bundle bundles = new Bundle();
                BChanging bchanging =  list_changings.get(fixedpos);

                // ensure your object has not null
                if (bchanging != null) {
                    bundles.putSerializable("bchanging", bchanging);
                    bundles.putDouble("mylat", latitude);
                    bundles.putDouble("mylongi", longitude);
                    // Log.e("friend", "is valid");
                } else {
                    Log.e("dip", "is null");
                }

                //Call to DetailedBChangingFragment
                Intent intent = new Intent(ListPlacesActivity.this, DetailBChangingActivityMapView.class);

                intent.putExtras(bundles); //Put your id to your next Intent
                startActivity(intent);
               // finish();



            }
        });

        lv_bchangings.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos =  position;
                BChanging bchanging =  list_changings.get(pos);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
                if(!PersistenceSQL.isFavourite(bchanging.getId(), getApplicationContext()))
                {
                    builder1.setMessage(getResources().getString(R.string.addfavourites));
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int fixedpos = pos;
                                    BChanging dip =  list_changings.get(fixedpos);
                                    addTofavourites(dip, getApplicationContext());
                                    lv_bchangings.setAdapter(new Adapter(getApplicationContext()     ,R.layout.list_item, list_changings));
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                }else
                {
                    builder1.setMessage(getResources().getString(R.string.removefavourites));
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int fixedpos = pos;
                                    BChanging dip =  list_changings.get(fixedpos);
                                    removeFromfavourites(dip);
                                    lv_bchangings.setAdapter(new Adapter(getApplicationContext(),R.layout.list_item, list_changings));
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                }



                AlertDialog alert11 = builder1.create();
                alert11.show();


                return true;
            }
        });
    }

    private void goToDetailBChanging()
    {

    }
    private void requestLocationPermission() {

        gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location net_loc = null, gps_loc = null;
        //Check, if we already have permission
        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            if (ActivityCompat.shouldShowRequestPermissionRationale(ListPlacesActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ListPlacesActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Display an AlertDialog with an explanation and a button to trigger the request.
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.permission_location_explanation))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat
                                        .requestPermissions(ListPlacesActivity.this, Utils.PERMISSIONS_LOCATION,
                                                Utils.REQUEST_LOCATION);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this, Utils.PERMISSIONS_LOCATION,
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
                    Log.i(TAG, "chosen Location: "+ "GPS");
                    m_DeviceLocation = gps_loc;
                    startpoint = new LatLng(latitude,longitude);
                    application.setStartpoint(startpoint);

                }

                else
                {
                    Log.i(TAG, "chosen Location: "+ "NETWORK");
                    m_DeviceLocation = net_loc;
                    startpoint = new LatLng(latitude,longitude);
                    application.setStartpoint(startpoint);
                }


                // I used this just to get an idea (if both avail, its upto you which you want to take as I taken location with more accuracy)

            } else {

                if (gps_loc != null) {
                    m_DeviceLocation = net_loc;
                } else if (net_loc != null) {
                    m_DeviceLocation = gps_loc;
                }
            }
            return;
        }
    }
    private void addTofavourites(BChanging bchanging, Context context)
    {
        PersistenceSQL.insertDip(context, bchanging);
        Utils.showAlert(context, "", "favourite baby changing already added");
        l_favourites = PersistenceSQL.getFavourites(this);
        application.setL_favourites(l_favourites);

        /*l_favourites = application.getL_favourites();
        if(!l_favourites.contains(dip))l_favourites.add(dip);
        application.setL_favourites(l_favourites);*/

    }

    private class CustomLocationListener implements LocationListener {

        public void onLocationChanged(Location argLocation) {
            Log.i("++++++++++","CustomLocationListener");
            m_DeviceLocation = argLocation;
            latitude = m_DeviceLocation.getLatitude();
            longitude = m_DeviceLocation.getLongitude();
            startpoint = new LatLng(latitude,longitude);
            application.setStartpoint(startpoint);



            //Location locAnte = distance_list[pos-1];


            mLocationManager.removeUpdates(this);
        }

        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {}
    }
    private void removeFromfavourites(BChanging bchanging)
    {
        PersistenceSQL.deleteDip(bchanging.getId(),this);
        Utils.showAlert(this, "", "baby changing already deleted");
        l_favourites = PersistenceSQL.getFavourites(this);
        application.setL_favourites(l_favourites);




    }

    private class Adapter extends ArrayAdapter<BChanging> {

        private ArrayList<BChanging> items;
        //private DecimalFormat df = new DecimalFormat("0.00");

        public Adapter(Context context, int textViewResourceId,
                       List<BChanging> items) {
            super(context, textViewResourceId, items);
            this.items = (ArrayList<BChanging>) items;

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }

            final BChanging p = items.get(position);


            if (p != null) {



                final TextView txt_nameplace = (TextView) v.findViewById(R.id.txt_nameplace);

                final TextView txt_distance = (TextView) v.findViewById(R.id.txt_distance);
                final ImageView img_photo = (ImageView)v.findViewById(R.id.img_photo);
                final ImageView img_fav = (ImageView) v.findViewById(R.id.img_fav);


                String name = p.getNameplace()+ ", ";
                String address = p.getProvince();



                int posstartname = 0;
                int posendname = name.length();
                //Log.i("posendname: ", String.valueOf(posendname));
                //int length_hasbean = hasbeanplace.length();

                Spannable wordtoSpan = new SpannableString(name +  address);
                wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), posstartname, posendname, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), posendname, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txt_nameplace.setText(wordtoSpan);
                //txt_nameplace.setText(name);
                //txt_address.setText(address);

                if(p.getDistance()!= null)
                {
                    txt_distance.setText(String.valueOf(p.getDistance())+ " KM");
                    txt_distance.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }


                if (img_photo != null) {

                    if(p.getUrlpic()!= null)
                    {

                        if(!p.getUrlpic().equals(""))
                        {
                            Bitmap bmp;
                            try {
                                String urlpic = AccessInterface.URL_GETPHOTO + p.getUrlpic();
                                Log.i("urlpic: ",urlpic);
                                bmp = BitmapFactory.decodeStream(new java.net.URL(urlpic).openStream());
                                if(bmp!=null)img_photo.setImageBitmap(bmp);
                                else
                                {
                                    img_photo.setBackgroundResource(R.drawable.ic_carritoitem_mdpi);
                                }
                            } catch (MalformedURLException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }



                    }else img_photo.setBackgroundResource(R.drawable.ic_carritoitem_mdpi);
                }

                if(PersistenceSQL.isFavourite(p.getId(), getContext()))
                {
                    img_fav.setVisibility(View.VISIBLE);
                }else img_fav.setVisibility(View.INVISIBLE);


            }

            return v;

        }



    }
    private void getListBChangings()
    {
        pDialog = ProgressDialog.show(this, getString(R.string.info), getString(R.string.loading));
        Thread thread = new Thread(new GetAllBChangings());
        thread.start();

    }
    private class GetAllBChangings implements Runnable {





        public void run() {
            Log.i(TAG, "GetAllDips");

            int mensajeDevuelto = RESULT_GET_OK;


            JSONObject jsonresponse = null;

            //String url = "places/"+latitude+"/"+longitude+"?"+"token="+ application.getTokenapp();
            String url = AccessInterface.URL_GETALL;
            jsonresponse = AccessInterface.sendJSON(url, null, "GET");
            if(jsonresponse!=null)
            {
                //  if(jsonresponse.has("code")){

                //String code = jsonresponse.getString("code");


                list_changings = AccessInterface.getListDips(jsonresponse);
                double distance = 0;
                //l_dips_aux = l_dips;

                //l_dips.clear();

                list_changings_aux = Utils.orderListByDistance(list_changings,distance, startpoint);
                Log.i("ordered l_dips : ", String.valueOf(list_changings_aux.size()));

                list_changings = list_changings_aux;
                application.setL_bchangings(list_changings);
                Log.i("list_changings: ", String.valueOf(list_changings.size()));

            }
            else
            {
                mensajeDevuelto = RESULT_GET_ERROR;


            }





            handler.sendEmptyMessage(mensajeDevuelto);
        }
    }

    private class ResultMessageCallback implements Handler.Callback {

        public boolean handleMessage(Message arg0) {

            //if(pDialog != null)pDialog.dismiss();
            Log.i(TAG, "ResultMessageCallback");

            switch (arg0.what) {



                case RESULT_GET_OK:
                    if(pDialog != null)pDialog.dismiss();

                    Log.i(TAG,"RESULT_GET_OK");

                    if(list_changings.size()>0)
                    {

                        lv_bchangings.setAdapter(new Adapter(getApplicationContext(),R.layout.list_item, list_changings));




                    }
                    else{
                        Utils.showAlert(ListPlacesActivity.this, "","No hay datos para mostrar");
                    }



                    break;
                case  RESULT_GET_ERROR:
                    if(pDialog != null)pDialog.dismiss();
                    Log.i(TAG,"RESULT_GET_ERROR");
                    Utils.showAlert(ListPlacesActivity.this, "",menserror);

                    break;






            }

            return true; // lo marcamos como procesado
        }
    }
}
