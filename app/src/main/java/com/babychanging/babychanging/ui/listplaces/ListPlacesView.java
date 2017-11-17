package com.babychanging.babychanging.ui.listplaces;

import com.babychanging.babychanging.data.model.BabyC;

import java.util.List;

/**
 * Created by vik on 23/10/2017.
 */

public interface ListPlacesView {

    void setupAdapter();
    void showProgress();
    void showList();
    void showMessage(String message);

    void hideProgress();

    void showItems(List<BabyC> items);
    //void showBC (BabyC babyc);



   // void goToDetailBC();


}
