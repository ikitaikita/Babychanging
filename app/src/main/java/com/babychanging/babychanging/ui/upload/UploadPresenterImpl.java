package com.babychanging.babychanging.ui.upload;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import com.babychanging.babychanging.data.api.BCService;
import com.babychanging.babychanging.data.model.BabyC;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by vik on 27/10/2017.
 */

public class UploadPresenterImpl implements UploadPresenter, UploadInteractor.OnCustomLocationListener {

    private UploadView uploadView;
    private UploadInteractor uploadInteractor;
    private final BCService service;
    private Context context;

    private Location mDeviceLocation;
    private double latitude;
    private double longitude;

    private BabyC babyc;

    public UploadPresenterImpl(Context context, Activity activity, UploadView uploadView, BCService service) {
        this.context = context;
        this.uploadView = uploadView;
        this.service = service;
        this.uploadInteractor = new UploadInteractorImpl(context, activity);

    }


    public Location getmDeviceLocation() {
        return mDeviceLocation;
    }

    public void setmDeviceLocation(Location mDeviceLocation) {
        this.mDeviceLocation = mDeviceLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public BabyC getBabyc() {
        return babyc;
    }

    public void setBabyc(BabyC babyc) {
        this.babyc = babyc;
    }


    @Override
    public void requestLocationPermissions() {
        uploadInteractor.requestLocation(this);


    }

    @Override
    public void clickCamera() {
        uploadView.showSourceImageSelection();


    }

    @Override
    public void clickUpload() {
        uploadView.showRate();

    }

    @Override
    public void uploadBabyC(String nameplace, String latitude, String longitude, String urlpic, String state, String province, String address) {
        babyc.setNameplace(nameplace);
        babyc.setLatitude(latitude);
        babyc.setLongitude(longitude);
        babyc.setUrlpic(urlpic);
        babyc.setState(state);
        babyc.setProvince(province);
        babyc.setAddress(address);
        service.saveBabyC(babyc, "addnew").enqueue(new Callback<BabyC>() {
            @Override
            public void onResponse(Call<BabyC> call, Response<BabyC> response) {
                if (response.isSuccessful()) {
                    Timber.i("response: ", response.body().toString());
                    uploadView.showSuccessUpload("Book data was successfully updated in the API.");
                    Timber.i("BabyC data was successfully updated in the API.");
                } else {
                    try {
                        Timber.i("The response failed: %s", response.errorBody().string());
                        uploadView.showErrorMessage("There was an error uploading BabyC data");
                    } catch (IOException ignored) {
                        // no op
                    }
                }
            }

            @Override
            public void onFailure(Call<BabyC> call, Throwable t) {
                uploadView.showErrorMessage("Unable to update the BabyC in the API.");
                Timber.e(t, "Unable to update the BabyC in the API.");
            }
        });
    }

    @Override
    public void setLocationDevice(Location newLocation) {
        this.mDeviceLocation = newLocation;

    }

    @Override
    public void setNewLatitudeOnDevice(double latitude) {
        this.latitude = latitude;

    }

    @Override
    public void setNewLongitudeOnDevice(double longitude) {
        this.longitude = longitude;

    }

    @Override
    public void showAlertDialog(String message) {
        uploadView.showAlertDialog(message);
    }
}


