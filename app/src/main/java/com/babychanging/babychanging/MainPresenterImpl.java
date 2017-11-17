package com.babychanging.babychanging;

/**
 * Created by vik on 23/10/2017.
 */

public class MainPresenterImpl implements MainPresenter {

    private MainView mainView;

    public MainPresenterImpl(MainView mainView) {
        this.mainView = mainView;

    }
    @Override
    public void navigateUpload() {
       mainView.openUpload();
    }

    @Override
    public void navigateListPlaces() {
        mainView.openListPlaces();

    }
}
