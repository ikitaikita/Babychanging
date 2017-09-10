
package com.babychanging.babychanging;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.app.AlertDialog;
import android.util.Base64;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.babychanging.babychanging.internal.AccessInterface;
import com.babychanging.babychanging.internal.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class UploadActivity extends Activity  implements View.OnClickListener{

    private static String TAG ="UploadActivity";
    private ImageView img_camera;
    private Button    btn_upload;
    private EditText edt_nameplace;

   // private ProgressDialog pDialogx;
    private Handler handler = new Handler(new ResultMessageCallback());
    private String menserror="Error";

    private final int RESULT_UPLOAD_OK= 1;
    private final int RESULT_UPLOAD_ERROR = -1;
    private String state ="";

    //GPS

    private LatLng startpoint;
    CustomLocationListener customLocationListener = new CustomLocationListener();
    private Location m_DeviceLocation = null;
    private LocationManager mLocationManager;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private double latitude = 42.598726;
    private double longitude = -5.567096;

    //Camera, Gallery
    private static int TAKE_PICTURE = 2;
    private static final int SELECT_PICTURE = 3;
    private String nameCap = "";
    private String namePhoto ="";
    private byte[] bitmapdata = null; //image
    private String encodedBase64 = "";
    private StringBuilder stringBuilder = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Request Permissions for Android 6.0
        requestLocationPermission();

      /*  Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            Double latitude = bundle.getDouble("latitude");
        }*/


        img_camera = (ImageView)findViewById(R.id.img_camera);
        btn_upload = (Button)findViewById(R.id.btn_upload);
        edt_nameplace = (EditText) findViewById(R.id.edt_nameplace);
        img_camera.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {


            case R.id.img_camera:
            {
                Toast toast1 =Toast.makeText(getApplicationContext(),"Camera", Toast.LENGTH_SHORT);
                toast1.show();

                GalleryCamSelection();
            }
            break;
            case R.id.btn_upload:
            {
                if(edt_nameplace.length()!= 0  || bitmapdata != null)
                {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.title)
                            .setSingleChoiceItems(Utils.choices, 0, null)
                            .setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                    //Log.i("seleccion: ", String.valueOf(selectedPosition));
                                    state = getState(selectedPosition);
                                    //Log.i("state: ", state);
                                    uploadData();
                                    gotoMainScreen();
                                    // Do something useful withe the position of the selected radio button
                                }
                            })
                            .setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();

                                }
                            })
                            .setCancelable(true)
                        /*.setNegativeButton(R.string.no_button_label,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //uploadData();
                                        gotoMainScreen();
                                    }
                                })*/
                            .show();
                }else
                {
                    Utils.showAlert(this,"", "Please introduce a name or a pic for the place and try again. Thanks");

                }


            }
            break;

        }
    }

    private String getState(int position)
    {
        String return_state = "";
        switch(position)
        {
            case 0:
                return_state = Utils.G;
                break;
            case 1:
                return_state = Utils.R;
                break;
            case 2:
                return_state = Utils.B;
                break;
            default:
                break;

        }
        return return_state;

    }

    private void GalleryCamSelection() {

        final String[] items = { "Camara", "Gallery" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Selección");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {// Camara
                    CaptureFoto();
                } else if (item == 1) {// Galeria
                    GallerySelection();
                }

            }
        });

        builder.create();
        builder.show();

    }
    private void GallerySelection() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PICTURE);
    }
    private void CaptureFoto() {
        /////

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, nameCap);
        Uri mCapturedImageURI  = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent camaraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, output);

        startActivityForResult(camaraIntent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TAKE_PICTURE) {

            if (data != null) {

                if (data.hasExtra("data")) {



                    Bitmap bitmap = ((Bitmap) data.getParcelableExtra("data"));
                    //ImageView iv = (ImageView) findViewById(R.id.ImageView1);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    //bitmap.compress(CompressFormat.JPEG, 100, bos);
                    Bitmap newbitmap = Utils.resizeImageToMax(bitmap,400,400);
                    //bitmap.setDensity(72);
                    newbitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    //ImageView imgReceipt = (ImageView) findViewById(R.id.img_photo);
                    //roundImage(newbitmap);
                    img_camera.setImageBitmap(newbitmap);





                    bitmapdata = bos.toByteArray();

                    //bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);


                }

            }
        } else if (requestCode == SELECT_PICTURE) {
            Uri selectedImage = data.getData();

            String path = getRealPathFromURI(selectedImage);
            namePhoto = path;
            //Log.i("namePhoto: ",namePhoto);

            try {

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//						BitmapFactory.Options options = new BitmapFactory.Options();
//						options.inSampleSize = (calculateInSampleSize(path, 480, 480));
//						options.inDensity = 72;
                //Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Bitmap newbitmap = Utils.resizeImageToMax(bitmap,400,400);
                newbitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                //bitmap.compress(CompressFormat.JPEG, 100, bos);
                bitmapdata = bos.toByteArray();
                //ImageView imgReceipt = (ImageView) findViewById(R.id.img_photo);
                //bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                img_camera.setImageBitmap(newbitmap);
                //roundImage(newbitmap);

            } catch (Exception e) {

            }
        }




    }
    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        android.database.Cursor cursor =this.managedQuery(contentUri, proj, // columns
                // to
                // return
                null, // clause rows
                null, // clause selection
                null); // order
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }
    private void gotoMainScreen()
    {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    private void uploadData()
    {
        //pDialogx = ProgressDialog.show(this,"Info", "Uploading...");
        Thread thread = new Thread(new UploadData());
        thread.start();

    }
    private class CustomLocationListener implements LocationListener {

        public void onLocationChanged(Location argLocation) {
            //Log.i("++++++++++","CustomLocationListener");
            m_DeviceLocation = argLocation;
            latitude = m_DeviceLocation.getLatitude();
            longitude = m_DeviceLocation.getLongitude();
           /* startpoint = new LatLng(latitude,longitude);
            application.setStartpoint(startpoint);*/



            //Location locAnte = distance_list[pos-1];


            mLocationManager.removeUpdates(this);
        }

        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {}
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Display an AlertDialog with an explanation and a button to trigger the request.
                new android.app.AlertDialog.Builder(this)
                        .setMessage(getString(R.string.permission_location_explanation))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat
                                        .requestPermissions(UploadActivity.this, Utils.PERMISSIONS_LOCATION,
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
                    m_DeviceLocation = gps_loc;
                    /*startpoint = new LatLng(latitude,longitude);
                    application.setStartpoint(startpoint);*/

                }

                else
                {
                    //Log.i(TAG, "chosen Location: "+ "NETWORK");
                    m_DeviceLocation = net_loc;
                    /*startpoint = new LatLng(latitude,longitude);
                    application.setStartpoint(startpoint);*/
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
    private class UploadData implements Runnable {





        public void run() {
            //Log.i(TAG, "UploadData");

            int mensajeDevuelto = RESULT_UPLOAD_OK;


            JSONObject jsonresponse = null;

            //String url = "places/"+latitude+"/"+longitude+"?"+"token="+ application.getTokenapp();
            String url = AccessInterface.URL_PUTPLACE;
            stringBuilder = new StringBuilder();
            if(bitmapdata != null)
            {
                //prueba = Base64.encode(bitmapdata, Base64.DEFAULT);
                encodedBase64 = Base64.encodeToString(bitmapdata, Base64.DEFAULT);

                stringBuilder.append("data:image/jpeg;base64,");
                stringBuilder.append(encodedBase64);
                //Log.i("encodedImage: ", encodedBase64.toString());
                //encodedBase64 = new String(Base64.encode(bitmapdata, Base64.DEFAULT));
            }
            else stringBuilder.append("");



            JSONObject request = new JSONObject();
            try {
                request.put("nameplace", edt_nameplace.getText().toString());
                request.put("latitude", latitude);
                request.put("longitude", longitude);


                request.put("urlpic", stringBuilder.toString());
                //request.put("urlpic", "");
                request.put("state", state);
                request.put("province", "");
                request.put("address", "");
                //Log.i("json request: ", request.toString());



                jsonresponse = AccessInterface.sendJSON(url, request, "POST");
                if(jsonresponse!=null)
                {
                    //  if(jsonresponse.has("code")){

                    //String code = jsonresponse.getString("code");
                    //Log.i("jsonresponse: ", jsonresponse.toString());
                    if(jsonresponse.has("result"))
                    {
                        mensajeDevuelto = RESULT_UPLOAD_OK;

                    }else mensajeDevuelto = RESULT_UPLOAD_ERROR;



                }
                else
                {
                    mensajeDevuelto = RESULT_UPLOAD_ERROR;


                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }







            handler.sendEmptyMessage(mensajeDevuelto);
        }
    }

    private class ResultMessageCallback implements Handler.Callback {

        public boolean handleMessage(Message arg0) {


            //Log.i(TAG, "ResultMessageCallback");

            switch (arg0.what) {



                case RESULT_UPLOAD_OK:
                    //if(pDialogx != null)pDialogx.dismiss();

                    //Log.i(TAG,"RESULT_GET_OK");
                   // String messageout = getResources().getString(R.string.successfull_load);

                    //Utils.showAlert(UploadActivity.this, "",messageout);
                    /*AlertDialog.Builder alert = new android.app.AlertDialog.Builder(UploadActivity.this);

                    alert.setMessage(messageout);
                    alert.setCancelable(true);

                    alert.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });


                    android.app.AlertDialog alertDialog = alert.create();
                    alertDialog.show();*/


                    break;
                case  RESULT_UPLOAD_ERROR:

                    //Log.i(TAG,"RESULT_GET_ERROR");
                    Utils.showAlert(UploadActivity.this, "",getResources().getString(R.string.error_upload));

                    break;






            }

            return true; // lo marcamos como procesado
        }
    }
}
