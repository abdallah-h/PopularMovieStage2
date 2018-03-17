package com.abdoh.popularmoviestage2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.abdoh.popularmoviestage2.Adapters.TrailersAdapter;
import com.abdoh.popularmoviestage2.Models.MovieInfo;
import com.abdoh.popularmoviestage2.Models.ReviewInfo;
import com.abdoh.popularmoviestage2.Models.TrailerInfo;
import com.abdoh.popularmoviestage2.database.MovieContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by abdoh.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    MovieInfo mMovieInfo;
    @BindView(R.id.release_date)
    TextView releaseDateTextView;
    @BindView(R.id.rating_average)
    TextView averageTextView;
    @BindView(R.id.title)
    TextView titleTextView;
    @BindView(R.id.plot)
    TextView plotTextView;
    @BindView(R.id.poster)
    ImageView posterImageView;
    @BindView(R.id.trailers_listView)
    ListView trailersListView;
    @BindView(R.id.bookmark_button)
    Button bookmarksButton;

    ArrayList<TrailerInfo> mTrailerInfos;
    ArrayList<ReviewInfo> mReviewInfos;

    TrailersAdapter trailersAdapter;

    private static final int LOADER_ID = 122;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_constraint);
        ButterKnife.bind(this);
        trailersAdapter = new TrailersAdapter(this);
        trailersListView.setAdapter(trailersAdapter);

        Intent callerIntent = getIntent();
        if (callerIntent.hasExtra(MovieInfo.EXTRA_MOVIE)){
            mMovieInfo = new MovieInfo(callerIntent.getBundleExtra(MovieInfo.EXTRA_MOVIE));
            titleTextView.setText(mMovieInfo.title);
            releaseDateTextView.setText(String.format(getString(R.string.release_date), mMovieInfo.release_date));
            averageTextView.setText(String.format(getString(R.string.vote_average), mMovieInfo.vote_average));
            plotTextView.setText(mMovieInfo.overview);
            final Bitmap[] posterBitmap = new Bitmap[1];
            Bundle args = new Bundle();
            if (mMovieInfo.isBookmarked(this)){
                bookmarksButton.setText(R.string.MARKED_AS_FAVORITE);
                bookmarksButton.setBackgroundColor(Color.parseColor("#ffffB300"));
                args.putBoolean("local",true);

            }else {
                bookmarksButton.setBackgroundColor(Color.parseColor("#ffffff"));
                Picasso.with(this).load(mMovieInfo.getPosterUri(getString(R.string.poster_size)))
                        .into(new Target(){
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                posterBitmap[0] = bitmap;
                                mMovieInfo.setPoster(posterBitmap[0]);
                                posterImageView.setImageBitmap(posterBitmap[0]);
                            }
                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }
                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                args.putBoolean("local",false);
            }
            posterImageView.setImageBitmap(mMovieInfo.getPoster());

            trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Uri uri = trailersAdapter.getTrailerUri(position);

                    if (uri != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }
            });
            getSupportLoaderManager().restartLoader(LOADER_ID, args, this);
        }
    }

    @OnClick(R.id.bookmark_button)
    public void markingFavorite(View v) {
        if (!mMovieInfo.isBookmarked(getApplicationContext())) {
            if (mMovieInfo.saveToBookmarks(getApplicationContext())) {
                bookmarksButton.setText(R.string.MARKED_AS_FAVORITE);
                bookmarksButton.setBackgroundColor(Color.parseColor(getString(R.string.MARKED_COLOR)));
            }
        } else {
            if (mMovieInfo.removeFromBookmarks(getApplicationContext())) {
                bookmarksButton.setText(R.string.MARK_AS_FAVORITE);
                bookmarksButton.setBackgroundColor(Color.parseColor(getString(R.string.NON_MARKED_COLOR)));
            }
        }
    }

    @OnClick(R.id.reviews_button)
    public void seeReviews(View v) {
        String reviewsString = ReviewInfo.arrayToString(mReviewInfos);
        Intent reviewsIntent = new Intent(getApplicationContext(), ReviewsActivity.class);
        reviewsIntent.putExtra(getString(R.string.reviews_intent_extra), reviewsString);
        startActivity(reviewsIntent);
    }

    //Handles the background parts using loader.
    @Override
    public Loader<Object> onCreateLoader(final int id, final Bundle args) {

        return new AsyncTaskLoader<Object>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Void loadInBackground() {

                if (args != null && args.size() != 0) {
                    boolean local = args.getBoolean("local");
                    long id = mMovieInfo.id;

                    if (!local) {
                        NetworkUtils networkUtils = new NetworkUtils();
                        URL requestTrailersUrl = networkUtils.buildTrailersUrl(id);
                        URL requestReviewsUrl = networkUtils.buildReviewsUrl(id);
                        try {
                            String JSONResponseTrailers = networkUtils.getResponseFromHttpUrl(requestTrailersUrl);
                            String JSONResponseReviews = networkUtils.getResponseFromHttpUrl(requestReviewsUrl);

                            mTrailerInfos = JsonUtils.ParseTrailersJsonData(JSONResponseTrailers);
                            mReviewInfos = JsonUtils.ParseReviewsJsonData(JSONResponseReviews);

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Cursor cursor = getContentResolver()
                                .query(MovieContract.MovieEntry.CONTENT_URI,
                                        new String[]{MovieContract.MovieEntry.MOVIE_TRAILERS, MovieContract.MovieEntry.MOVIE_REVIEWS, MovieContract.MovieEntry.MOVIE_POSTER},
                                        MovieContract.MovieEntry.MOVIE_ID + "=?",
                                        new String[]{Long.toString(id)}, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            mTrailerInfos = TrailerInfo.stringToArray(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_TRAILERS)));
                            mReviewInfos = ReviewInfo.stringToArray(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_REVIEWS)));
                            mMovieInfo.setPosterFromCursor(cursor);
                            cursor.close();
                        }

                    }
                }

                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mMovieInfo.setTrailers(mTrailerInfos);
        mMovieInfo.setReviews(mReviewInfos);
        posterImageView.setImageBitmap(mMovieInfo.getPoster());
        if (mTrailerInfos !=null){
            trailersAdapter.setTrailers(mTrailerInfos);
            setListViewHeightBasedOnChildren(trailersListView);
        }

    }
    @Override
    public void onLoaderReset(Loader loader) {

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        TrailersAdapter trailersAdapter = (TrailersAdapter) listView.getAdapter();
        if (trailersAdapter == null) {
            return;
        }

        if (trailersAdapter.getCount()>0) {
            View listItem = trailersAdapter.getView(0, null, listView);
            listItem.measure(0,0);

            int totalHeight = listItem.getMeasuredHeight() * (trailersAdapter.getCount()+2);

            ViewGroup.LayoutParams params = listView.getLayoutParams();

            params.height = totalHeight
                    + (listView.getDividerHeight() * (trailersAdapter.getCount()-1));

            listView.setLayoutParams(params);
        }
    }
}
