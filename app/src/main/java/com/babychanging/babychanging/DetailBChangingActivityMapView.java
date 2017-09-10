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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
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
public class DetailBChangingActivityMapView extends FragmentActivity implements OnClickListener {
    private static final String TAG = "DetailDipFragment";

    private BChanging bchanging = null;
    private double mylat,mylongi = 0;
    private MyApplication application;

    private TextView txt_name;
    private TextView txt_address;
    private TextView txt_state;
   // private Button btn_goto;
    private ImageButton img_pic;
    private Button btn_share;

    //lay_map
    private RelativeLayout lay_map;

    private TextView txt_distance_val;
    private TextView txt_duration_val;
    String distance="";
    String duration="";


    private MapView mMapView;
    private GoogleMap mMap = null;


    //lay_camera
    private RelativeLayout lay_pic;
    private ImageView img_photo;
    private ImageButton img_btnclose;





    //private ImageView img_photo;
    //private TextView txt_desc;




    private double latitude = 42.598726;
    private double longitude = -5.567096;


    public DetailBChangingActivityMapView() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail_bchanging);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        application = (MyApplication) getApplicationContext();
        try {
            MapsInitializer.initialize(DetailBChangingActivityMapView.this);


        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }


        //Log.e(TAG, "onCreate");



        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            bchanging= (BChanging) bundle.getSerializable("bchanging");
            mylat = bundle.getDouble("mylat");
            mylongi = bundle.getDouble("mylongi");

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
        img_pic = (ImageButton) findViewById(R.id.img_pic);
        img_pic.setOnClickListener(this);
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(this);
        txt_name = (TextView)findViewById(R.id.txt_name);
        txt_address = (TextView)findViewById(R.id.txt_address);

        txt_state = (TextView)findViewById(R.id.txt_state);
        //layPIC

        lay_pic = (RelativeLayout) findViewById(R.id.lay_pic);
        img_photo = (ImageView)findViewById(R.id.img_photo);
        img_btnclose = (ImageButton)  findViewById(R.id.img_btnclose);
        img_btnclose.setOnClickListener(this);

        //lay_map

        lay_map = (RelativeLayout)  findViewById(R.id.lay_map);
        //lay_map.setVisibility(View.GONE);



        txt_duration_val = (TextView) findViewById(R.id.txt_duration_val);
        txt_distance_val = (TextView) findViewById(R.id.txt_distance_val);

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






        if(bchanging != null)

        {

            txt_name.setText(bchanging.getNameplace());
            String type_string = Utils.getStateFromChar(bchanging.getState());
            //Log.i("555555: ", type_string);
            txt_state.setText(type_string);
            if(!bchanging.getAddress().equals(""))txt_address.setText(bchanging.getAddress());
            LatLng coordinate = new LatLng(Double.valueOf(bchanging.getLatitude()), Double.valueOf( bchanging.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate,12));
            mMap.setMyLocationEnabled(true);
            drawOnePointOnMap();



                if(bchanging.getUrlpic()!= null)
                {

                    if(!bchanging.getUrlpic().equals(""))
                    {
                        Bitmap bmp;
                        try {
                            String urlpic = AccessInterface.URL_GETPHOTO + bchanging.getUrlpic();
                            //Log.i("urlpic: ",urlpic);
                            bmp = BitmapFactory.decodeStream(new URL(urlpic).openStream());
                            if(bmp!=null)img_pic.setImageBitmap(bmp);
                            else
                            {
                                img_pic.setBackgroundResource(R.drawable.ic_carritoitem_mdpi);
                            }
                        } catch (MalformedURLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }



                }else img_pic.setBackgroundResource(R.drawable.ic_carritoitem_mdpi);



            if(mylat != 0  & mylongi != 0)
            {
                Location myLocation = mMap.getMyLocation();
                //Log.i("KKKKKK: ", myLocation.toString());
                //LatLng origin = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                LatLng origin = new LatLng(mylat, mylongi);
                LatLng dest = new LatLng(Double.parseDouble(bchanging.getLatitude()), Double.parseDouble(bchanging.getLongitude()));

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


        if(bchanging.getLatitude() != null && bchanging.getLongitude() != null)
        {
            LatLng markerPosition = new LatLng (Double.parseDouble(bchanging.getLatitude()) , Double.parseDouble(bchanging.getLongitude()));
            //Log.i("markerPosition: ", markerPosition.toString());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition,15));
            String descrip ="";
            //String id = p.getID();
            //descrip = p.getGuid();

            //añade el marcador al mapa
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(markerPosition)
                    .title(bchanging.getNameplace())
                    .snippet(bchanging.getAddress())
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
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.ic_noimage_300);
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
                lay_pic.setVisibility(View.GONE);
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
                lay_pic.setVisibility(View.VISIBLE);
                new ImageDownloaderTaskUser(img_photo).execute(AccessInterface.URL_GETPHOTO + bchanging.getUrlpic());

            }
            break;

            case R.id.img_btnclose:
            {

                lay_pic.setVisibility(View.GONE);


            }
            break;

            case R.id.btn_share:
            {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareSubject =  getResources().getString(R.string.share_subject);
                String uri = "http://maps.google.com/maps?q=" +bchanging.getLatitude()+","+bchanging.getLongitude();

                String shareBody =getResources().getString(R.string.share_body)+ " " + bchanging.getNameplace() +  " "  + getResources().getString(R.string.hope_like);
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
        txt_distance_val.setText(distance);
        txt_duration_val.setText(duration);
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
