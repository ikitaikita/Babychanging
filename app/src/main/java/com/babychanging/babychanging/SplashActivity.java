package com.babychanging.babychanging;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    public static final String TAG = SplashActivity.class.getSimpleName();
    private static final int SLEEP_TIME = 2;
    private ImageView mSplashImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSplashImage = (ImageView) findViewById(R.id.splashImage);
        int orient = getWindowManager().getDefaultDisplay().getOrientation();
        if (orient == 0) {
            mSplashImage.setBackgroundResource(R.drawable.splash_portrait);
        } else {
            mSplashImage.setBackgroundResource(R.drawable.splash_landscape);
        }
        Thread thread = new Thread(new SleepWorker());
        thread.start();
    }

    private void goToMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    private class SleepWorker implements Runnable {

        public void run(){
            try {
                Thread.sleep(SLEEP_TIME * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            goToMainActivity();
        }
    }

   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orient = getWindowManager().getDefaultDisplay().getOrientation();
        if (orient == 0) {
            splashImage.setBackgroundResource(R.drawable.splash_portrait);
        } else {
            splashImage.setBackgroundResource(R.drawable.splash_landscape);
        }
    }*/
}
