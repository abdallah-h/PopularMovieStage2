package com.abdoh.popularmoviestage2.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.abdoh.popularmoviestage2.Models.TrailerInfo;
import com.abdoh.popularmoviestage2.R;

import java.util.ArrayList;

/**
 * Created by abdoh.
 */

public class TrailersAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TrailerInfo> mTrailerInfos;

    public TrailersAdapter(Context context){
        this.context = context;
        this.mTrailerInfos = new ArrayList<>();
    }

    private void clear(){
        mTrailerInfos.clear();
        notifyDataSetChanged();
    }

    public void setTrailers(ArrayList<TrailerInfo> trailers){
        clear();
        mTrailerInfos.addAll(trailers);
        notifyDataSetChanged();
    }

    public void addTrailers(ArrayList<TrailerInfo> trailers){
        mTrailerInfos.addAll(trailers);
        notifyDataSetChanged();
    }
    public Uri getTrailerUri(int position){
        TrailerInfo trailerInfo = getItem(position);
        if (trailerInfo !=null){
            return Uri.parse(trailerInfo.url);
        }
        return null;
    }
    @Override
    public int getCount() {
        return mTrailerInfos.size();
    }

    @Override
    public TrailerInfo getItem(int position) {
        if (position>=0 && position< mTrailerInfos.size()){
            return mTrailerInfos.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (getItem(position) == null){
            return -1L;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View trailerItem = convertView;
        TrailerInfo trailerInfo = getItem(position);
        if(trailerItem==null){
            try{
                LayoutInflater vi;
                vi = LayoutInflater.from(context);
                trailerItem = vi.inflate(R.layout.trailer_list_item,parent,false);

            }catch (Exception e){
                Log.e(context.getClass().getSimpleName(),e.toString());
            }
        }
        if (trailerItem != null) {
            ((TextView) trailerItem.findViewById(R.id.tv_trailer_item_title)).setText(trailerInfo.title);
        }
        return  trailerItem;
    }
}
