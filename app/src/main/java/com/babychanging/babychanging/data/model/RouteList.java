package com.babychanging.babychanging.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vik on 26/10/2017.
 */

public class RouteList {

        @SerializedName("routes")
        @Expose
        private List<Route> routes = null;
        @SerializedName("status")
        @Expose
        private String status;
        public List<Route> getRoutes() {return routes; }

        public void setRoutes(List<Route> routes) { this.routes = routes; }

        public String getStatus() { return status;  }

        public void setStatus(String status) {
            this.status = status;
        }








}
