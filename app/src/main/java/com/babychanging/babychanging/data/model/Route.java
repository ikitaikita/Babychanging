package com.babychanging.babychanging.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vik on 26/10/2017.
 */

public class Route {



        @SerializedName("legs")
        @Expose
        private List<Leg> legs = null;

        public List<Leg> getLegs() {
            return legs;
        }

        public void setLegs(List<Leg> legs) {
            this.legs = legs;
        }

        public class Leg {
            @SerializedName("distance")
            @Expose
            private Distance distance;
            @SerializedName("duration")
            @Expose
            private Duration duration;

            @SerializedName("steps")
            @Expose
            private List<Step> steps = null;



            public Distance getDistance() {
                return distance;
            }

            public void setDistance(Distance distance) {
                this.distance = distance;
            }

            public Duration getDuration() {
                return duration;
            }

            public void setDuration(Duration duration) {
                this.duration = duration;
            }


            public List<Step> getSteps() {
                return steps;
            }

            public void setSteps(List<Step> steps) {
                this.steps = steps;
            }

            public class Distance {
                @SerializedName("text")
                @Expose
                private String text;
                @SerializedName("value")
                @Expose
                private Integer value;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public Integer getValue() {
                    return value;
                }

                public void setValue(Integer value) {
                    this.value = value;
                }
            }
            public class Duration {
                @SerializedName("text")
                @Expose
                private String text;
                @SerializedName("value")
                @Expose
                private Integer value;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public Integer getValue() {
                    return value;
                }

                public void setValue(Integer value) {
                    this.value = value;
                }
            }
            public class Step {
                @SerializedName("distance")
                @Expose
                private Distance distance;
                @SerializedName("duration")
                @Expose
                private Duration duration;

                @SerializedName("html_instructions")
                @Expose
                private String htmlInstructions;
                @SerializedName("polyline")
                @Expose
                private Polyline polyline;

                @SerializedName("travel_mode")
                @Expose
                private String travelMode;

                public Distance getDistance() {
                    return distance;
                }

                public void setDistance(Distance distance) {
                    this.distance = distance;
                }

                public Duration getDuration() {
                    return duration;
                }

                public void setDuration(Duration duration) {
                    this.duration = duration;
                }


                public String getHtmlInstructions() {
                    return htmlInstructions;
                }

                public void setHtmlInstructions(String htmlInstructions) {
                    this.htmlInstructions = htmlInstructions;
                }

                public Polyline getPolyline() {
                    return polyline;
                }

                public void setPolyline(Polyline polyline) {
                    this.polyline = polyline;
                }



                public String getTravelMode() {
                    return travelMode;
                }

                public void setTravelMode(String travelMode) {
                    this.travelMode = travelMode;
                }


            }

        }


}
