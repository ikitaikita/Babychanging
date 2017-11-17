package com.babychanging.babychanging.ui.detailbabyc;

import com.babychanging.babychanging.data.model.BabyC;
import com.babychanging.babychanging.data.model.Route;

import java.util.List;

/**
 * Created by vik on 25/10/2017.
 */

public interface DetailBChangingView {


    void showBigPic();
    void closeBigPic();
    void showDistanceDuration(long distance, long duration);
    void showMap();
    void drawPointOnMap();
    void showDurationAndDistance(List<Route> routelist);
    void showMessage(String message);
    void shareBabyC();
}
