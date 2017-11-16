package com.babychanging.babychanging;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Intent;
import android.os.AsyncTask;
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


import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
//import android.util.Log;
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
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListPlacesActivity extends Activity {

    public static final String TAG = ListPlacesActivity.class.getSimpleName();
    public final int RESULT_GET_ERROR = -1;
    public final int RESULT_GET_OK = 1;

    private ListView mLv_bchangings;
    private ArrayList<BChanging> mList_changings = new ArrayList();
    private ArrayList<BChanging> mList_changings_aux = new ArrayList();
    private ArrayList<BChanging> mL_favourites= new ArrayList<BChanging>();
    private MyApplication mApplication;

    private ProgressDialog mDialog;
    private Handler mHandler = new Handler(new ResultMessageCallback());

    private String mError = "Error";

    //GPS
    private LatLng mStartpoint;
    private Location mDeviceLocation = null;
    private LocationManager mLocationManager;
    CustomLocationListener customLocationListener = new CustomLocationListener();
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
        mApplication = (MyApplication) getApplicationContext();
        mLv_bchangings = (ListView)findViewById(R.id.list);

        //CustomLocationListener customLocationListener = new CustomLocationListener();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Request Permissions for Android 6.0
        requestLocationPermission();


        if(mDeviceLocation!=null)
        {

            //Log.i(TAG, "tengo localizacion");

            latitude = mDeviceLocation.getLatitude();
            longitude = mDeviceLocation.getLongitude();

        }
        else
        {
            //Log.i(TAG, "waiting location....");
        }
        //Log.i(TAG, "latitude: "+ String.valueOf(latitude));
        //Log.i(TAG, "longitude: "+  String.valueOf(longitude));
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        getListBChangings();

        mLv_bchangings.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                int fixedpos = arg2;





                Bundle bundles = new Bundle();
                BChanging bchanging =  mList_changings.get(fixedpos);

                // ensure your object has not null
                if (bchanging != null) {
                    bundles.putSerializable("bchanging", bchanging);
                    bundles.putDouble("mylat", latitude);
                    bundles.putDouble("mylongi", longitude);
                    // Log.e("friend", "is valid");
                } else {
                    //Log.e("dip", "is null");
                }

                //Call to DetailedBChangingFragment
                Intent intent = new Intent(ListPlacesActivity.this, DetailBChangingFragmentActivity.class);

                intent.putExtras(bundles); //Put your id to your next Intent
                startActivity(intent);
               // finish();



            }
        });

        mLv_bchangings.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos =  position;
                BChanging bchanging =  mList_changings.get(pos);
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
                                    BChanging dip =  mList_changings.get(fixedpos);
                                    addTofavourites(dip, getApplicationContext());
                                    mLv_bchangings.setAdapter(new NewAdapter(getApplicationContext()     ,R.layout.item_list, mList_changings));
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
                                    BChanging dip =  mList_changings.get(fixedpos);
                                    removeFromfavourites(dip);
                                    mLv_bchangings.setAdapter(new NewAdapter(getApplicationContext(),R.layout.item_list, mList_changings));
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
                    //Log.i(TAG, "chosen Location: "+ "GPS");
                    mDeviceLocation = gps_loc;
                    mStartpoint = new LatLng(latitude,longitude);
                    mApplication.setStartpoint(mStartpoint);

                }

                else
                {
                    //Log.i(TAG, "chosen Location: "+ "NETWORK");
                    mDeviceLocation = net_loc;
                    mStartpoint = new LatLng(latitude,longitude);
                    mApplication.setStartpoint(mStartpoint);
                }


                // I used this just to get an idea (if both avail, its upto you which you want to take as I taken location with more accuracy)

            } else {

                if (gps_loc != null) {
                    mDeviceLocation = net_loc;
                } else if (net_loc != null) {
                    mDeviceLocation = gps_loc;
                }
            }
            return;
        }
    }
    private void addTofavourites(BChanging bchanging, Context context)
    {
        PersistenceSQL.insertDip(context, bchanging);
        Utils.showAlert(context, "", "favourite baby changing already added");
        mL_favourites = PersistenceSQL.getFavourites(this);
        mApplication.setL_favourites(mL_favourites);

        /*mL_favourites = application.getmL_favourites();
        if(!mL_favourites.contains(dip))mL_favourites.add(dip);
        application.setmL_favourites(mL_favourites);*/

    }

    private class CustomLocationListener implements LocationListener {

        public void onLocationChanged(Location argLocation) {
            //Log.i("++++++++++","CustomLocationListener");
            mDeviceLocation = argLocation;
            latitude = mDeviceLocation.getLatitude();
            longitude = mDeviceLocation.getLongitude();
            mStartpoint = new LatLng(latitude,longitude);
            mApplication.setStartpoint(mStartpoint);



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
        mL_favourites = PersistenceSQL.getFavourites(this);
        mApplication.setL_favourites(mL_favourites);




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
                v = vi.inflate(R.layout.item_list, null);
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
                                //Log.i("urlpic: ",urlpic);
                                bmp = BitmapFactory.decodeStream(new java.net.URL(urlpic).openStream());
                                if(bmp!=null)img_photo.setImageBitmap(bmp);
                                else
                                {
                                    img_photo.setBackgroundResource(R.drawable.ic_noimage_small);
                                }
                            } catch (MalformedURLException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }else img_photo.setBackgroundResource(R.drawable.ic_noimage_small);


                    }else img_photo.setBackgroundResource(R.drawable.ic_noimage_small);
                }

                if(PersistenceSQL.isFavourite(p.getId(), getContext()))
                {
                    img_fav.setVisibility(View.VISIBLE);
                }else img_fav.setVisibility(View.INVISIBLE);


            }

            return v;

        }



    }

    private class NewAdapter extends ArrayAdapter<BChanging> {

        private ArrayList<BChanging> items;
        //private DecimalFormat df = new DecimalFormat("0.00");

        public NewAdapter(Context context, int textViewResourceId,
                       List<BChanging> items) {
            super(context, textViewResourceId, items);
            this.items = (ArrayList<BChanging>) items;

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //View v = convertView;
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.item_list, null);
                holder = new ViewHolder();
                holder.txt_nameplace = (TextView) convertView.findViewById(R.id.txt_nameplace);

                holder.txt_distance = (TextView) convertView.findViewById(R.id.txt_distance);
                holder.img_photo = (ImageView)convertView.findViewById(R.id.img_photo);
                holder.img_fav = (ImageView) convertView.findViewById(R.id.img_fav);
                convertView.setTag(holder);


            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            BChanging p = items.get(position);
            String name = p.getNameplace()+ ", ";
            String address = p.getProvince();



            int posstartname = 0;
            int posendname = name.length();
            //Log.i("posendname: ", String.valueOf(posendname));
            //int length_hasbean = hasbeanplace.length();

            Spannable wordtoSpan = new SpannableString(name +  address);
            wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), posstartname, posendname, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), posendname, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.txt_nameplace.setText(wordtoSpan);
            //txt_nameplace.setText(name);
            //txt_address.setText(address);

            if(p.getDistance()!= null)
            {
                holder.txt_distance.setText(String.valueOf(p.getDistance())+ " KM");
                holder.txt_distance.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            if(PersistenceSQL.isFavourite(p.getId(), getContext()))
            {
                holder.img_fav.setVisibility(View.VISIBLE);
            }else holder.img_fav.setVisibility(View.INVISIBLE);

            if (holder.img_photo != null) {
                if(p.getUrlpic().equals(""))
                    holder.img_photo.setBackgroundResource(R.drawable.ic_noimage_small);
                    else   new ImageDownloaderTask(holder.img_photo).execute(AccessInterface.URL_GETPHOTO + p.getUrlpic());
            }








            return convertView;

        }



    }
    private static class ViewHolder {


        TextView txt_nameplace;
        TextView txt_distance;
        ImageView img_photo;
        ImageView img_fav;
    }
    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        //Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.ic_carritoitem_mdpi);
                        //imageView.setImageDrawable(placeholder);
                        imageView.setBackgroundResource(R.drawable.ic_noimage_small);

                    }
                }
            }
        }
    }
    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            //Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
    private void getListBChangings()
    {
        mDialog = ProgressDialog.show(this, getString(R.string.info), getString(R.string.loading));
        Thread thread = new Thread(new GetAllBChangings());
        thread.start();

    }
    private class GetAllBChangings implements Runnable {





        public void run() {
            //Log.i(TAG, "GetAllDips");

            int mensajeDevuelto = RESULT_GET_OK;


            JSONObject jsonresponse = null;

            //String url = "places/"+latitude+"/"+longitude+"?"+"token="+ mApplication.getTokenapp();
            String url = AccessInterface.URL_GETALL;
            jsonresponse = AccessInterface.sendJSON(url, null, "GET");
            if(jsonresponse!=null)
            {
                //  if(jsonresponse.has("code")){

                //String code = jsonresponse.getString("code");


                mList_changings = AccessInterface.getListDips(jsonresponse);
                double distance = 0;
                //l_dips_aux = l_dips;

                //l_dips.clear();

                mList_changings_aux = Utils.orderListByDistance(mList_changings,distance, mStartpoint);
                //Log.i("ordered l_dips : ", String.valueOf(mList_changings_aux.size()));

                mList_changings = mList_changings_aux;
                mApplication.setL_bchangings(mList_changings);
                //Log.i("mList_changings: ", String.valueOf(mList_changings.size()));

            }
            else
            {
                mensajeDevuelto = RESULT_GET_ERROR;


            }


            mHandler.sendEmptyMessage((mensajeDevuelto));


        }
    }

    private class ResultMessageCallback implements Handler.Callback {

        public boolean handleMessage(Message arg0) {

            //if(mDialog != null)mDialog.dismiss();
            //Log.i(TAG, "ResultMessageCallback");

            switch (arg0.what) {



                case RESULT_GET_OK:
                    if(mDialog != null)mDialog.dismiss();

                    //Log.i(TAG,"RESULT_GET_OK");

                    if(mList_changings.size()>0)
                    {

                        mLv_bchangings.setAdapter(new NewAdapter(getApplicationContext(),R.layout.item_list, mList_changings));




                    }
                    else{
                        Utils.showAlert(ListPlacesActivity.this, "","No hay datos para mostrar");
                    }



                    break;
                case  RESULT_GET_ERROR:
                    if(mDialog != null)mDialog.dismiss();
                    //Log.i(TAG,"RESULT_GET_ERROR");
                    Utils.showAlert(ListPlacesActivity.this, "",mError);

                    break;






            }

            return true; // lo marcamos como procesado
        }
    }
}
