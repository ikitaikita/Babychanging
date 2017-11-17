package com.babychanging.babychanging.ui.listplaces;

import com.babychanging.babychanging.data.api.BCService;
import com.babychanging.babychanging.data.model.BabyC;
import com.babychanging.babychanging.data.model.BabyCList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by vik on 23/10/2017.
 */

public class ListPlacesPresenterImpl implements ListPlacesPresenter{

    private ListPlacesView listplacesView;
    private final BCService service;


    public ListPlacesPresenterImpl (ListPlacesView listplacesView, BCService service)
    {
        this.listplacesView = listplacesView;
        this.service = service;
    }

  /*  @Override
    public void onResume() {
        if (listplacesView != null) {
            listplacesView.showProgress();
        }

        //initDataSet();
    }*/

 /*   @Override
    public void onItemClicked(BabyC bc) {
        if (listplacesView != null) {
            listplacesView.showMessage(String.format("BabyC %d clicked",bc.getNameplace()));
        }

    }*/

    @Override
    public void onDestroy() {
        listplacesView = null;
    }

    @Override
    public void initDataSet ()
    {
        listplacesView.showProgress();

        service.getBCList("mostrar_bchangingplaces").enqueue( new Callback<BabyCList>()
        {
            @Override
            public void onResponse (Call<BabyCList> call, Response<BabyCList> response)
            {
                if ( response.isSuccessful() )
                {
                    //listplacesView.hideProgress();
                    listplacesView.showItems(response.body().getDatos() );

                    listplacesView.hideProgress();
                    Timber.i( "Books data was loaded from API." );
                }
            }

            @Override
            public void onFailure (Call<BabyCList> call, Throwable t)
            {
                //listplacesView.hideProgress();
                listplacesView.showMessage("Error message.Unable to load the books data from API ");
                Timber.e( t, "Unable to load the books data from API." );
            }
        } );
    }



    public ListPlacesView getMainView() {
        return listplacesView;
    }

   /* @Override
    public void onBCClick(BabyC bc) {
        listplacesView.showBC(bc);


    }*/
}
