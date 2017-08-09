package com.babychanging.babychanging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{

    private ImageView img_search, img_upload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_search = (ImageView)findViewById(R.id.img_search);
        img_search.setOnClickListener(this);
        img_upload = (ImageView)findViewById(R.id.img_upload);
        img_upload.setOnClickListener(this);
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
        Intent intent = new Intent(this, ListPlacesActivity.class);
        this.startActivity(intent);



    }
}
