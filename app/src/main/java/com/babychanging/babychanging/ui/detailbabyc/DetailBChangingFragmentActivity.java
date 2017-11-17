package com.babychanging.babychanging.ui.detailbabyc;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.babychanging.babychanging.Constants;
import com.babychanging.babychanging.R;
import com.babychanging.babychanging.data.model.BabyC;
import com.babychanging.babychanging.data.model.Route;
import com.babychanging.babychanging.di.Injector;
import com.babychanging.babychanging.internal.DirectionsJSONParser;
import com.babychanging.babychanging.internal.MyApplication;
import com.babychanging.babychanging.utils.Utils;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailBChangingFragmentActivity extends FragmentActivity implements DetailBChangingView,OnClickListener {
    public static final String TAG = DetailBChangingFragmentActivity.class.getSimpleName();
    public static final String EXTRA_BABYC = "bchanging";
    public static final String EXTRA_LATITUDE = "laitude";
    public static final String EXTRA_LONGITUDE = "longitude";

    private DetailBChangingPresenter presenter;

    private BabyC babyC;
    //
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




    private double mLatitude = 42.598726;
    private double longitude = -5.567096;


    public DetailBChangingFragmentActivity() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail_bchanging);
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/
        //mApplication = (MyApplication) getApplicationContext();
        try {
            MapsInitializer.initialize(DetailBChangingFragmentActivity.this);


        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        presenter = new DetailBChangingPresenter(this, Injector.provideRouteMapService()) ;
        loadView();
        presenter.setBabyC( (BabyC)getIntent().getSerializableExtra( EXTRA_BABYC ) );
        presenter.setmLat(getIntent().getDoubleExtra(EXTRA_LATITUDE, 0));
        presenter.setmLongi(getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0));



        //Log.e(TAG, "onCreate");

        //babyC = (BabyC)getIntent().getSerializableExtra(EXTRA_BABYC);

        loadMap(savedInstanceState);
        loadBC(presenter.getBabyC());
        drawBConMap(presenter.getBabyC(), presenter.getmLat(), presenter.getmLongi());


      /*  Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {

            presenter.setBabyC((BabyC) bundle.getSerializable(EXTRA_BABYC));
            presenter.setmLat(bundle.getDouble(EXTRA_LATITUDE));
            presenter.setmLongi(bundle.getDouble(EXTRA_LONGITUDE));

        }*/
        /*requestLocationPermission();

        if(m_DeviceLocation!=null)
        {
            Log.i(TAG, "tengo localizacion");

            latitude = m_DeviceLocation.getLatitude();
            longitude = m_DeviceLocation.getLongitude();
        }*/
       // btn_goto = (Button) findViewById(R.id.btn_goto);
        //btn_goto.setOnClickListener(this);










    }
   private void loadMap(Bundle savedInstanceState){
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
   }

   private void loadView(){
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


   }




    public void loadBC(BabyC babyC) {
        this.babyC = babyC;
        mTxt_name.setText(babyC.getNameplace());
        String type_string = Utils.getStateFromChar(babyC.getState());
        //Log.i("555555: ", type_string);
        mTxt_state.setText(type_string);
        if(!babyC.getAddress().equals(""))mTxt_address.setText(babyC.getAddress());

        if(babyC.getUrlpic()!= null) {

            if (!babyC.getUrlpic().equals("")) {
                Picasso.with(this)
                        .load(Constants.BASE_URL_PIC + babyC.getUrlpic())
                        .noFade()
                        .into(mImg_pic);


            }
        }





    }

    private void drawBConMap(BabyC babyc, double mLat, double mLongi){
        LatLng coordinate = new LatLng(Double.valueOf(babyc.getLatitude()), Double.valueOf( babyc.getLongitude()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate,12));
        mMap.setMyLocationEnabled(true);
        if(babyc.getLatitude() != null && babyc.getLongitude() != null)
        {
            LatLng markerPosition = new LatLng (Double.parseDouble(babyc.getLatitude()) , Double.parseDouble(babyc.getLongitude()));
            //Log.i("markerPosition: ", markerPosition.toString());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition,15));
            String descrip ="";
            //String id = p.getID();
            //descrip = p.getGuid();

            //añade el marcador al mapa
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(markerPosition)
                    .title(babyc.getNameplace())
                    .snippet(babyc.getAddress())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))

                    .anchor(0.5f, 0.5f));

            marker.showInfoWindow();
        }
        if(mLat != 0  & mLongi != 0)
        {
            Location myLocation = mMap.getMyLocation();
            //Log.i("KKKKKK: ", myLocation.toString());
            //LatLng origin = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            /*LatLng origin = new LatLng(mLat, mLongi);
            LatLng dest = new LatLng(Double.parseDouble(babyC.getLatitude()), Double.parseDouble(babyC.getLongitude()));*/
            String origin = Utils.getStringPointFromDouble(presenter.getmLat(), presenter.getmLongi());
            String destination = Utils.getStringPointFromString(presenter.getBabyC().getLatitude(), presenter.getBabyC().getLongitude());
            presenter.loadRouteData(origin,destination);

        }
    }

    @Override
    public void showBigPic() {
        mRel_pic.setVisibility(View.VISIBLE);
        Picasso.with(this)
                .load(Constants.BASE_URL_PIC + babyC.getUrlpic())
                .noFade()
                .into(mImg_photo);
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
               /* Toast toast1 =Toast.makeText(getApplicationContext(),"img_pic", Toast.LENGTH_SHORT);
                toast1.show();
                showBigPic();*/
                presenter.clickSmallPic();


            }
            break;

            case R.id.img_btnclose:
            {
                presenter.clickClosePic();
                //closeBigPic();



            }
            break;

            case R.id.btn_share:
            {
                presenter.shareButtonClick();


            }
            break;
            default:break;


        }
    }


    @Override
    public void closeBigPic() {
        mRel_pic.setVisibility(View.GONE);
    }




    @Override
    public void showDistanceDuration(long distance, long duration) {

    }

    @Override
    public void showMap() {

    }

    @Override
    public void drawPointOnMap() {
        //Log.i("", "drawOnePointOnMap");








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

    @Override
    public void showDurationAndDistance(List<Route> routelist) {
        String duration = routelist.get(0).getLegs().get(0).getDuration().getText();
        String distance = routelist.get(0).getLegs().get(0).getDistance().getText();
        mTxt_distance_val.setText(distance);
        mTxt_duration_val.setText(duration);



    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }

    @Override
    public void shareBabyC() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareSubject =  getResources().getString(R.string.share_subject);
        String uri = "http://maps.google.com/maps?q=" +babyC.getLatitude()+","+babyC.getLongitude();

        String shareBody =
                getResources().getString(R.string.share_body)
                        + " "
                        + babyC.getNameplace()
                        +  " "
                        + " que se encuentra en "
                        + uri
                        +  " "
                        + getResources().getString(R.string.hope_like);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,shareSubject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

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
            //addDistanceDuration();
        }
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
