package com.babychanging.babychanging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.babychanging.babychanging.ui.listplaces.ListPlacesActivity;
import com.babychanging.babychanging.ui.upload.UploadActivity;

public class MainActivity extends Activity implements MainView,View.OnClickListener{
    public static final String TAG = MainActivity.class.getSimpleName();
    private ImageView mImg_search, mImg_upload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.img_search).setOnClickListener(this);
        findViewById(R.id.img_upload).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {


            case R.id.img_search:
            {
                openListPlaces();
            }
            break;
            case R.id.img_upload:
            {
                openUpload();
            }
            break;

        }
    }





    @Override
    public void openListPlaces() {
        startActivity(new Intent(this, ListPlacesActivity.class));
        //finish();
    }

    @Override
    public void openUpload() {
        startActivity(new Intent(this, UploadActivity.class));
        //finish();

    }
}
