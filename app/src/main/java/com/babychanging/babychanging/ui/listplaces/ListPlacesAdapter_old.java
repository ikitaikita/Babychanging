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
import com.babychanging.babychanging.model.BChanging;
import com.babychanging.babychanging.persistence.PersistenceSQL;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ListPlacesAdapter_old extends ArrayAdapter<BChanging> {

    private final Context context;




    public ListPlacesAdapter_old(Context context, ArrayList<BChanging> listbc) {
        super(context, 0, listbc);
        this.context =context;



    }


    private static class ViewHolder  {

        TextView txt_nameplace;
        TextView txt_distance;
        ImageView img_photo;
        ImageView img_fav;


    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        BChanging p = getItem(position);

        Resources res = getContext().getResources();
        if (view == null) {
            holder = new ViewHolder();


            view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);



            holder.txt_nameplace = (TextView) view.findViewById(R.id.txt_nameplace);
            holder.txt_distance = (TextView) view.findViewById(R.id.txt_distance);
            holder.img_photo = (ImageView) view.findViewById(R.id.img_photo);
            holder.img_fav = (ImageView) view.findViewById(R.id.img_fav);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Get the post for the current position.

        String name = p.getNameplace()+ ", ";
        String address = p.getProvince();



        int posstartname = 0;
        int posendname = name.length();
        //Log.i("posendname: ", String.valueOf(posendname));
        //int length_hasbean = hasbeanplace.length();

        Spannable wordtoSpan = new SpannableString(name +  address);
        wordtoSpan.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorPrimary)), posstartname, posendname, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorAccent)), posendname, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.txt_nameplace.setText(wordtoSpan);


        if(p.getDistance()!= null)
        {
            holder.txt_distance.setText(String.valueOf(p.getDistance())+ " KM");
            holder.txt_distance.setTextColor(res.getColor(R.color.colorPrimaryDark));
        }

        if(PersistenceSQL.isFavourite(p.getId(), getContext()))
        {
            holder.img_fav.setVisibility(View.VISIBLE);
        }else holder.img_fav.setVisibility(View.INVISIBLE);

        if(p.getUrlpic()!= null)
        {
            if(!p.getUrlpic().equals(""))
            {
                // Download of the featuredImage url asynchronously into the image view.
                Picasso.with(getContext())
                        .load(Constants.BASE_URL_PIC + p.getUrlpic())
                        .noFade()
                        .into(holder.img_photo);
            }
            else holder.img_photo.setBackgroundResource(R.drawable.ic_noimage_small);
        }


        return view;
    }





    public interface BabyCItemListener
    {
        void onBCClick(long id);

        void onBCLongClick(long id);
    }
}
