package com.babychanging.babychanging.ui.listplaces;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
//import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.babychanging.babychanging.data.api.BCService;
import com.babychanging.babychanging.data.model.BabyC;
import com.babychanging.babychanging.di.Injector;
import com.babychanging.babychanging.ui.detailbabyc.DetailBChangingFragmentActivity;
import com.babychanging.babychanging.R;
import com.babychanging.babychanging.ui.listplaces.divider.SimpleDividerDecoration;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ListPlacesActivity extends Activity implements ListPlacesView {

    public static final String TAG = ListPlacesActivity.class.getSimpleName();


    //private ListView mLv_bchangings;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private ListPlacesPresenter presenter;

    private ListPlacesAdapter adapter ;
    //private OnBabyCClickListener onbabylistener;



    private double latitude = 42.598726;
    private double longitude = -5.567096;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);

        //mApplication = (MyApplication) getApplicationContext();
        /*mLv_bchangings = (ListView)findViewById(R.id.list);
        mLv_bchangings.setOnItemClickListener(this);*/

        progressBar = (ProgressBar) findViewById(R.id.progress);
        presenter = new ListPlacesPresenterImpl(this, Injector.provideBCService() );

        adapter = new ListPlacesAdapter(this, new ArrayList<BabyC>( 0 ), onbabylistener);

        //mLv_bchangings.setAdapter(adapter);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.addItemDecoration(new SimpleDividerDecoration(this));

        configureLayout();






        //CustomLocationListener customLocationListener = new CustomLocationListener();



    }
    private void configureLayout(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( this );
        mRecyclerView.setLayoutManager( layoutManager );
        setupAdapter();


    }
    @Override protected void onResume() {
        super.onResume();
        //presenter.onResume();
        presenter.initDataSet();
    }
    @Override protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setupAdapter() {
        mRecyclerView.setAdapter( adapter );

    }

    @Override public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void showList() {

    }

    @Override public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void showItems(List<BabyC> items) {
        adapter.updateList( items );

    }

  /*  @Override
    public void showBC(BabyC babyc) {
        startActivity(new Intent(this, DetailBChangingFragmentActivity.class));
        finish();

    }
*/
    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();


    }



    private OnBabyCClickListener onbabylistener = new OnBabyCClickListener() {
        @Override
        public void onBCClick(BabyC babyc) {
            Timber.d( "edit babyc with name", babyc.getNameplace() );
            Toast.makeText(ListPlacesActivity.this, "goToDetail", Toast.LENGTH_LONG).show();

                Intent intent = new Intent( ListPlacesActivity.this, DetailBChangingFragmentActivity.class );
                Bundle bundles = new Bundle();
                bundles.putSerializable(DetailBChangingFragmentActivity.EXTRA_BABYC, babyc);
                bundles.putDouble(DetailBChangingFragmentActivity.EXTRA_LATITUDE, latitude);
                bundles.putDouble(DetailBChangingFragmentActivity.EXTRA_LONGITUDE, longitude);
                intent.putExtras(bundles);
                startActivity(intent);


        }
    };


}
