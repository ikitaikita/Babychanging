package com.babychanging.babychanging.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vik on 23/10/2017.
 */

public class BabyC implements Serializable{

    @SerializedName("id")
    @Expose
    private String id;


    @SerializedName("nameplace")
    @Expose
    private String nameplace;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("urlpic")
    @Expose
    private String urlpic;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("province")
    @Expose
    private String province;
    @SerializedName("address")
    @Expose
    private String address;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameplace() {
        return nameplace;
    }

    public void setNameplace(String nameplace) {
        this.nameplace = nameplace;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUrlpic() {
        return urlpic;
    }

    public void setUrlpic(String urlpic) {
        this.urlpic = urlpic;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    private Double distance;


}
