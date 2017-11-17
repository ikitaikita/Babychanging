package com.babychanging.babychanging.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vik on 26/10/2017.
 */

public class Polyline {


        @SerializedName("points")
        @Expose
        private String points;

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }

}
