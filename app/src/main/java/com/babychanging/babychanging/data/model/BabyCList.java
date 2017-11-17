package com.babychanging.babychanging.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by vik on 23/10/2017.
 */

public class BabyCList {

    @SerializedName("datos")
    @Expose
    private ArrayList<BabyC> datos = new ArrayList();

    public ArrayList<BabyC> getDatos() {
        return datos;
    }

    public void setDatos(ArrayList<BabyC> datos) {
        this.datos = datos;
    }
}
