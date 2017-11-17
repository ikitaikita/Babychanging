/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.babychanging.babychanging.ui.listplaces;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.babychanging.babychanging.Constants;
import com.babychanging.babychanging.R;
import com.babychanging.babychanging.data.model.BabyC;
import com.babychanging.babychanging.internal.AccessInterface;
import com.babychanging.babychanging.model.BChanging;
import com.babychanging.babychanging.persistence.PersistenceSQL;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;



public class ListPlacesAdapter extends RecyclerView.Adapter<ListPlacesAdapter.ViewHolder> {

    private final Context context;
    private List<BabyC> listbc;
    private final OnBabyCClickListener listener;


    public ListPlacesAdapter(Context context, List<BabyC> listbc, OnBabyCClickListener onBabyCClickListener) {
       // super(context, 0, listbc);
        this.context =context;
        this.listbc = listbc;
        this.listener = onBabyCClickListener;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
        return new ViewHolder(v, this.listener);


    }

    @Override
    public void onBindViewHolder(ViewHolder customViewHolder, int i ) {

        BabyC bc = listbc.get(i);
        Resources res = context.getResources();

        //Render image using Picasso library

        if(bc.getUrlpic()!= null)
        {
            if(!bc.getUrlpic().equals(""))
            {
                // Download of the featuredImage url asynchronously into the image view.
                Picasso.with(context)
                        .load(Constants.BASE_URL_PIC + bc.getUrlpic())
                        .noFade()
                        .into(customViewHolder.img_photo);

            }
           //else customViewHolder.img_photo.setBackgroundResource(R.drawable.ic_noimage_small);
            else customViewHolder.img_photo.setImageResource(R.drawable.ic_noimage_small);

        }
        else customViewHolder.img_photo.setImageResource(R.drawable.ic_noimage_small);

        //Setting text view title

        String name = bc.getNameplace()+ ", ";
        String address = bc.getProvince();



        int posstartname = 0;
        int posendname = name.length();
        //Log.i("posendname: ", String.valueOf(posendname));
        //int length_hasbean = hasbeanplace.length();

        Spannable wordtoSpan = new SpannableString(name +  address);
        wordtoSpan.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorPrimary)), posstartname, posendname, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorAccent)), posendname, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        customViewHolder.txt_nameplace.setText(wordtoSpan);


       /* if(bc.getDistance()!= null)
        {
            customViewHolder.txt_distance.setText(String.valueOf(bc.getDistance())+ " KM");
            customViewHolder.txt_distance.setTextColor(res.getColor(R.color.colorPrimaryDark));
        }*/

        if(PersistenceSQL.isFavourite(bc.getId(), context))
        {
            customViewHolder.img_fav.setVisibility(View.VISIBLE);
        }else customViewHolder.img_fav.setVisibility(View.INVISIBLE);

    }



   @Override
   public int getItemCount() {
       return listbc.size();
   }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView txt_nameplace;
        private TextView txt_distance;
        private ImageView img_photo;
        private ImageView img_fav;

        OnBabyCClickListener itemListener;

        public ViewHolder(View view, OnBabyCClickListener listener) {
            super(view);

            this.txt_nameplace = (TextView) view.findViewById(R.id.txt_nameplace);
            this.txt_distance = (TextView) view.findViewById(R.id.txt_distance);
            this.img_photo = (ImageView) view.findViewById(R.id.img_photo);
            this.img_fav = (ImageView) view.findViewById(R.id.img_fav);
            this.itemListener = listener;
            view.setOnClickListener( this );
        }


        @Override
        public void onClick(View v) {
            BabyC bc = getItem( getAdapterPosition() );
            this.itemListener.onBCClick(bc);
        }
    }



    private BabyC getItem(int adapterPosition) {
        return listbc.get(adapterPosition);
    }


    public void updateList(List<BabyC> listbc) {
        this.listbc = listbc;
        notifyDataSetChanged();
    }



}
