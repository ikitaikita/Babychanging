package com.babychanging.babychanging.internal;

//import android.util.Log;

import com.babychanging.babychanging.model.BChanging;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by vik on 24/05/2017.
 */

public class AccessInterface {
    public static final String TAG = "AccessInterface";
    private static final String API_KEY = "&key=AIzaSyBjKG1ULUOUPq1f1K4c28U3lonN7ilxh7M";
    public static String URL_GETALL = "http://esunescandalo.com/appbabychanging/datos.class.php?tipo=mostrar_bchangingplaces";
    public static String URL_PUTPLACE = "http://esunescandalo.com/appbabychanging/datos.class.php?tipo=addnew";

    public static String URL_GETGOOGLE_PLACEID = "https://maps.googleapis.com/maps/api/geocode/json?latlng=42.537896,-6.518599&key=AIzaSyCQKKIegyFLPV-OtpN_QzxCNIzwc-Zkmp8";
    public static String URL_GETPHOTO="http://www.esunescandalo.com/appbabychanging/imgbabychanging/";

    public static JSONObject sendJSON(String url, JSONObject jObjentrada, String method)
    {
        HttpURLConnection conn = null;
        OutputStreamWriter wr;

        StringBuilder result = new StringBuilder();
        URL urlObj;
        //JSONArray jArray;
        JSONObject jObj = null;
        //String paramsString;
        //StringBuilder  sbParams = new StringBuilder();


        if (method.equals("POST")) {
            // request method is POST
            try {
                urlObj = new URL(url);
                //Log.i("ACCESO POST: ", urlObj.toString());

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(true);
                conn.setDoInput(true);

                conn.setRequestMethod("POST");

                //conn.setRequestProperty("Accept-Charset", "UTF-8");
                //conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Content-Type", "application/json");

                //conn.setRequestProperty("Accept", "application/json");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.connect();

//                paramsString = sbParams.toString();
//                Log.i("paramsString: ", paramsString);
                if(jObjentrada != null)
                {
                    wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(jObjentrada.toString());
                    wr.flush();
                }




            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if(method.equals("GET")){
            // request method is GET



            try {

                urlObj = new URL(url);
                //Log.i("ACCESO GET: ", urlObj.toString());

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(false);

                conn.setRequestMethod("GET");

                //conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("Accept", "application/json");

                conn.setConnectTimeout(15000);

                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }



        try {
            //Receive the response from the server
            //StringBuilder sb = new StringBuilder();
            int HttpResult = conn.getResponseCode();
            //Log.i("HttpResult", String.valueOf(HttpResult));
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    //Log.i("line: ", line);
                    result.append(line + "\n");
                }
                br.close();
                //Log.i("", result.toString());
                System.out.println("" + result.toString());
            } else {


                //Log.e("error:", conn.getResponseMessage());
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    //Log.i("line: ", line);
                    result.append(line + "\n");
                }
                br.close();
                //Log.i("", result.toString());
                System.out.println(conn.getResponseMessage());
            }

//            InputStream in = new BufferedInputStream(conn.getInputStream());
//            if(in != null)
//            {
//            	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                //result = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                	Log.i("line", line);
//                    result.append(line);
//                }
//            }





            //Log.d("JSON Parser", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.disconnect();

        // try parse the string to a JSON object
        try {

            jObj = new JSONObject(result.toString());

        } catch (JSONException e) {
            //Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON Object
        return jObj;
    }

    public static ArrayList<BChanging> getListDips(JSONObject jsonObject)
    {
        ArrayList<BChanging> bchangingList = new ArrayList<BChanging>();


        try {
            //JSONObject jsonObject = new JSONObject(JSONResponse);

            JSONArray array_data = jsonObject.getJSONArray("datos");


            for(int i = 0; i < array_data.length(); i++) {
                JSONObject elem = array_data.getJSONObject(i);

                    BChanging dip = new BChanging();

                    String id = elem.getString("id");
                    String lat = elem.getString("latitude");
                    String lng = elem.getString("longitude");
                    String nameplace = elem.getString("nameplace");
                    String province = elem.getString("province");
                    String urlpic = elem.getString("urlpic");
                    String state = elem.getString("state");
                    String address = elem.getString("address");

                    dip.setId(id);
                    dip.setNameplace(nameplace);
                    dip.setUrlpic(urlpic);
                    dip.setState(state);
                    dip.setLatitude(lat);
                    dip.setLongitude(lng);
                    dip.setProvince(province);
                    dip.setAddress(address);


                    bchangingList.add(dip);

                    //Log.i(TAG, "bchanging added: " + dip.getId());
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bchangingList;

    }

}
