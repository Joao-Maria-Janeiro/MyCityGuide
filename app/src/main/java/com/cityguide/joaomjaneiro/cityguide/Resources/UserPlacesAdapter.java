package com.cityguide.joaomjaneiro.cityguide.Resources;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cityguide.joaomjaneiro.cityguide.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserPlacesAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> placeName;
    private ArrayList<String> imgUrl;

    public UserPlacesAdapter(Context mContext, ArrayList<String> placeName, ArrayList<String> imgUrl) {
        this.mContext = mContext;
        this.placeName = placeName;
        this.imgUrl = imgUrl;
    }

    @Override
    public int getCount() {
        return placeName.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(mContext, R.layout.place_info, null);
        TextView tvPlaceName = v.findViewById(R.id.placeInfo);
        ImageView imgPlace = v.findViewById(R.id.imgViewPlacesList);

        tvPlaceName.setText(placeName.get(i));
        Picasso.get().load(imgUrl.get(i)).into(imgPlace);

        return v;
    }
}
