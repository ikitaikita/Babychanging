package com.babychanging.babychanging.ui.upload;

/**
 * Created by vik on 27/10/2017.
 */

public interface UploadPresenter {
    void requestLocationPermissions();
    void clickCamera();
    double getLatitude();
    double getLongitude();
    void clickUpload();
    void uploadBabyC(String nameplace, String latitude, String longitude, String urlpic, String state, String province, String address);

}
