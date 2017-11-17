package com.babychanging.babychanging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.babychanging.babychanging.ui.listplaces.ListPlacesActivity;
import com.babychanging.babychanging.ui.listplaces.ListPlacesActivity_old;
import com.babychanging.babychanging.ui.upload.UploadActivity;

public class MainActivity_old extends Activity implements View.OnClickListener{
    public static final String TAG = MainActivity_old.class.getSimpleName();
    private ImageView mImg_search, mImg_upload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImg_search = (ImageView)findViewById(R.id.img_search);
        mImg_search.setOnClickListener(this);
        mImg_upload = (ImageView)findViewById(R.id.img_upload);
        mImg_upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {


            case R.id.img_search:
            {
                goToListScreen();
            }
            break;
            case R.id.img_upload:
            {
                goToUploadScreen();
            }
            break;

        }
    }

    private void goToUploadScreen()
    {
        Intent intent = new Intent(this, UploadActivity.class);
        this.startActivity(intent);

    }
    private void goToListScreen()
    {
        Intent intent = new Intent(this, ListPlacesActivity_old.class);
        this.startActivity(intent);




    }
}
