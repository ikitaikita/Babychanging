package com.babychanging.babychanging;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babychanging.babychanging.internal.AccessInterface;
import com.babychanging.babychanging.internal.DirectionsJSONParser;
import com.babychanging.babychanging.internal.MyApplication;
import com.babychanging.babychanging.internal.Utils;
import com.babychanging.babychanging.model.BChanging;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailBChangingFragmentActivity extends FragmentActivity implements OnClickListener {
    public static final String TAG = DetailBChangingFragmentActivity.class.getSimpleName();

    private BChanging mBchanging = null;
    private double mLat,mLongi = 0;
    private MyApplication mApplication;

    private TextView mTxt_name;
    private TextView mTxt_address;
    private TextView mTxt_state;
   // private Button btn_goto;
    private ImageButton mImg_pic;
    private Button mBtn_share;

    //lay_map
    private RelativeLayout mRel_map;

    private TextView mTxt_distance_val;
    private TextView mTxt_duration_val;
    String distance="";
    String duration="";


    private MapView mMapView;
    private GoogleMap mMap = null;


    //lay_camera
    private RelativeLayout mRel_pic;
    private ImageView mImg_photo;
    private ImageButton mImg_btnclose;





    //private ImageView mImg_photo;
    //private TextView txt_desc;




  /*  private double mLatitude = 42.598726;
    private double longitude = -5.567096;*/


    public DetailBChangingFragmentActivity() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail_bchanging);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mApplication = (MyApplication) getApplicationContext();
        try {
            MapsInitializer.initialize(DetailBChangingFragmentActivity.this);


        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }


        //Log.e(TAG, "onCreate");



        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            mBchanging= (BChanging) bundle.getSerializable("bchanging");
            mLat = bundle.getDouble("mylat");
            mLongi = bundle.getDouble("mylongi");

        }
        /*requestLocationPermission();

        if(m_DeviceLocation!=null)
        {
            Log.i(TAG, "tengo localizacion");

            latitude = m_DeviceLocation.getLatitude();
            longitude = m_DeviceLocation.getLongitude();
        }*/
       // btn_goto = (Button) findViewById(R.id.btn_goto);
        //btn_goto.setOnClickListener(this);
        mImg_pic = (ImageButton) findViewById(R.id.img_pic);
        mImg_pic.setOnClickListener(this);
        mBtn_share = (Button) findViewById(R.id.btn_share);
        mBtn_share.setOnClickListener(this);
        mTxt_name = (TextView)findViewById(R.id.txt_name);
        mTxt_address = (TextView)findViewById(R.id.txt_address);

        mTxt_state = (TextView)findViewById(R.id.txt_state);
        //layPIC

        mRel_pic = (RelativeLayout) findViewById(R.id.lay_pic);
        mImg_photo = (ImageView)findViewById(R.id.img_photo);
        mImg_btnclose = (ImageButton)  findViewById(R.id.img_btnclose);
        mImg_btnclose.setOnClickListener(this);

        //lay_map

        mRel_map = (RelativeLayout)  findViewById(R.id.lay_map);
        //lay_map.setVisibility(View.GONE);



        mTxt_duration_val = (TextView) findViewById(R.id.txt_duration_val);
        mTxt_distance_val = (TextView) findViewById(R.id.txt_distance_val);

        //get map
        mMapView = (MapView) findViewById(R.id.map);
        // mMapView.onCreate(mBundle);
        mMapView.onCreate(savedInstanceState);
        mMap = mMapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        //mMap.setMyLocationEnabled(true);




        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); //establecemos tipo de mapa

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);






        if(mBchanging != null)

        {

            mTxt_name.setText(mBchanging.getNameplace());
            String type_string = Utils.getStateFromChar(mBchanging.getState());
            //Log.i("555555: ", type_string);
            mTxt_state.setText(type_string);
            if(!mBchanging.getAddress().equals(""))mTxt_address.setText(mBchanging.getAddress());
            LatLng coordinate = new LatLng(Double.valueOf(mBchanging.getLatitude()), Double.valueOf( mBchanging.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate,12));
            mMap.setMyLocationEnabled(true);
            drawOnePointOnMap();



                if(mBchanging.getUrlpic()!= null)
                {

                    if(!mBchanging.getUrlpic().equals(""))
                    {
                        Bitmap bmp;
                        try {
                            String urlpic = AccessInterface.URL_GETPHOTO + mBchanging.getUrlpic();
                            //Log.i("urlpic: ",urlpic);
                            bmp = BitmapFactory.decodeStream(new URL(urlpic).openStream());
                            if(bmp!=null)mImg_pic.setImageBitmap(bmp);
                            else
                            {
                                mImg_pic.setBackgroundResource(R.drawable.ic_noimage_small);
                            }
                        } catch (MalformedURLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }



                }else mImg_pic.setBackgroundResource(R.drawable.ic_noimage_small);



            if(mLat != 0  & mLongi != 0)
            {
                Location myLocation = mMap.getMyLocation();
                //Log.i("KKKKKK: ", myLocation.toString());
                //LatLng origin = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                LatLng origin = new LatLng(mLat, mLongi);
                LatLng dest = new LatLng(Double.parseDouble(mBchanging.getLatitude()), Double.parseDouble(mBchanging.getLongitude()));

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);
                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        }
    }






    private void drawOnePointOnMap() {
        //Log.i("", "drawOnePointOnMap");


        if(mBchanging.getLatitude() != null && mBchanging.getLongitude() != null)
        {
            LatLng markerPosition = new LatLng (Double.parseDouble(mBchanging.getLatitude()) , Double.parseDouble(mBchanging.getLongitude()));
            //Log.i("markerPosition: ", markerPosition.toString());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition,15));
            String descrip ="";
            //String id = p.getID();
            //descrip = p.getGuid();

            //añade el marcador al mapa
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(markerPosition)
                    .title(mBchanging.getNameplace())
                    .snippet(mBchanging.getAddress())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))

                    .anchor(0.5f, 0.5f));

            marker.showInfoWindow();
        }





//						mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
//							@Override
//							 public void onInfoWindowClick(Marker marker) {
//								Place res = allMarkersMap.get(marker);
//								Intent intent = new Intent(getActivity(), MapPopupActivity.class);
//								//Store res = l_stores.get(Integer.parseInt(marker.getSnippet()));
//								//Store res = l_stores.get(Integer.parseInt(marker.getSnippet()));
//								intent.putExtra("store",res);
//								startActivity(intent);
//							 }
//						});


    }

    class ImageDownloaderTaskUser extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTaskUser(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            return downloadBitmapUser(params[0]);
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

                        //imageView.setImageDrawable(Utils.roundImageDrawable(bitmap, getActivity().getResources()));

                    } else {
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.ic_noimage_big);
                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
    }
    private Bitmap downloadBitmapUser(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //Bitmap bitmap2 = Utils.getRoundedShape(bitmap);
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

    @Override
    public void onResume() {
        super.onResume();
        //Log.i(TAG, "onResume");
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.i(TAG, "onPause");
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        //Log.i(TAG, "onDestroy");
        super.onDestroy();
        mMapView.onDestroy();

    }
    @Override
    public void onLowMemory() {

        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {

/*
            case R.id.btn_goto:
            {
                *//*String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", 42.598726, -5.567096, "Home Sweet Home", dip.getLatitude(), dip.getLongitude(), "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);*//*

                //handle multiple view click events
                //private double latitude = 42.598726;
                //private double longitude = -5.567096;
                mRel_pic.setVisibility(View.GONE);
                lay_map.setVisibility(View.VISIBLE);
                Location myLocation = mMap.getMyLocation();
                Log.i("KKKKKK: ", myLocation.toString());
                LatLng origin = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                LatLng dest = new LatLng(Double.parseDouble(bchanging.getLatitude()), Double.parseDouble(bchanging.getLongitude()));

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);
                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);



            }*/
            //break;
            case R.id.img_pic:

            {
                Toast toast1 =Toast.makeText(getApplicationContext(),"img_pic", Toast.LENGTH_SHORT);
                toast1.show();
                mRel_pic.setVisibility(View.VISIBLE);
                new ImageDownloaderTaskUser(mImg_photo).execute(AccessInterface.URL_GETPHOTO + mBchanging.getUrlpic());

            }
            break;

            case R.id.img_btnclose:
            {

                mRel_pic.setVisibility(View.GONE);


            }
            break;

            case R.id.btn_share:
            {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareSubject =  getResources().getString(R.string.share_subject);
                String uri = "http://maps.google.com/maps?q=" +mBchanging.getLatitude()+","+mBchanging.getLongitude();

                String shareBody =getResources().getString(R.string.share_body)+ " " + mBchanging.getNameplace() +  " "  + getResources().getString(R.string.hope_like);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,shareSubject);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

            }
            break;
            default:break;


        }
    }
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // start loading animation maybe?
            //adapter.clear(); // clear "old" entries (optional)
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                //Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String,String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
                distance = parser.getDistance();
                duration = parser.getDuration();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

           /* ldd = result.get(0);
            for (int i = 0; i < ldd.size(); i++) {

                HashMap dd = ldd.get(i);
                distance = (String)dd.get("distance");
                duration = (String)dd.get("duration");
            }*/


            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble((String)point.get("lat"));
                    double lng = Double.parseDouble((String)point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
            addDistanceDuration();
        }
    }

    private void addDistanceDuration()
    {
        //lay_dd.setVisibility(View.VISIBLE);
        mTxt_distance_val.setText(distance);
        mTxt_duration_val.setText(duration);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=true";
        String language ="language=en";
        String mode = "units=metric";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + language +  "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "http://maps.google.com/maps/api/directions/json?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
