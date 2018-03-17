package com.abdoh.popularmoviestage2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.abdoh.popularmoviestage2.Adapters.ReviewsAdapter;
import com.abdoh.popularmoviestage2.Models.ReviewInfo;

import java.util.ArrayList;

/**
 * Created by abdoh.
 */

public class ReviewsActivity extends AppCompatActivity {
    ListView reviewsListView;
    ReviewsAdapter mAdapter;
    TextView mErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        reviewsListView = (ListView) findViewById(R.id.reviews_listView);
        mErrorTextView = (TextView) findViewById(R.id.tv_reviews_error);
        mAdapter = new ReviewsAdapter(this);
        Intent intent = getIntent();

        reviewsListView.setAdapter(mAdapter);

        if (intent.hasExtra(getString(R.string.reviews_intent_extra))){
            ArrayList<ReviewInfo> reviews = ReviewInfo.stringToArray(intent.getStringExtra(getString(R.string.reviews_intent_extra)));

            if (reviews.size()>0) {
                mAdapter.setReviews(reviews);
            }else{
                reviewsListView.setVisibility(View.GONE);
                mErrorTextView.setVisibility(View.VISIBLE);
            }

        }
    }
}
