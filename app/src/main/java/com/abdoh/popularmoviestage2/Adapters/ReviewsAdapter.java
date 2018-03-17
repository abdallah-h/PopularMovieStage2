package com.abdoh.popularmoviestage2.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.abdoh.popularmoviestage2.Models.ReviewInfo;
import com.abdoh.popularmoviestage2.R;

import java.util.ArrayList;

/**
 * Created by abdoh.
 */

public class ReviewsAdapter extends BaseAdapter {
    private ArrayList<ReviewInfo> reviews;
    private Context context;
    public ReviewsAdapter(Context context){
        this.context = context;
        reviews = new ArrayList<>();
    }

    public void setReviews(ArrayList<ReviewInfo> data){
        reviews.clear();
        reviews.addAll(data);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public ReviewInfo getItem(int position) {
        if (position>=0 && position< reviews.size()){
            return reviews.get(position);
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
        View reviewItem = convertView;
        ReviewInfo reviewInfo = getItem(position);
        if(reviewItem==null){
            try{
                LayoutInflater vi;
                vi = LayoutInflater.from(context);
                reviewItem = vi.inflate(R.layout.review_list_item,null);

            }catch (Exception e){
                Log.e(context.getClass().getSimpleName(),e.toString());
            }
        }
        assert reviewItem != null;
        ((TextView) reviewItem.findViewById(R.id.author)).setText(reviewInfo.author);
        ((TextView) reviewItem.findViewById(R.id.content)).setText(reviewInfo.content);
        return  reviewItem;
    }
}
