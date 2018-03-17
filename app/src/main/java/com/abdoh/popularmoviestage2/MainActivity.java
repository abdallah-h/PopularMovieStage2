package com.abdoh.popularmoviestage2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.abdoh.popularmoviestage2.Adapters.MovieAdapter;
import com.abdoh.popularmoviestage2.Models.MovieInfo;
import com.abdoh.popularmoviestage2.database.MovieContract;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abdoh.
 */

public class MainActivity extends AppCompatActivity implements MovieAdapter.onPosterClickHandler,LoaderManager.LoaderCallbacks<ArrayList<MovieInfo>> {

    @BindView(R.id.posters_list)
    RecyclerView recyclerView;
    @BindView(R.id.POSTERS_ERROR)
    TextView errorTextView;
    @BindView(R.id.no_bookmarks)
    TextView noBookmarksTextView;
    @BindView(R.id.posters_progress_bar)
    ProgressBar progressBar;
    @BindString(R.string.movie_preferences)
    String MOVIE_PREFERENCES;
    @BindString(R.string.pref_sorting_key)
    String MOVIE_SORTING;
    @BindString(R.string.pref_sorting_popular)
    String MOVIE_SORTING_POPULAR;
    @BindString(R.string.pref_bookmarked)
    String MOVIE_BOOKMARKED;

    int loader;

    MovieAdapter movieAdapter;

    GridLayoutManager gridLayoutManager;

    AlertDialog alertDialog;

    SharedPreferences sharedPreferences;

    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;

    String actualCriterion;

    private static final int LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        }

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this);

        recyclerView.setAdapter(movieAdapter);

        loader = 0;

        alertDialog = initSortingDialog();

        initSharedPreferences();
        if (savedInstanceState != null) {
            movieAdapter.restoreInstanceState(savedInstanceState);
            recyclerView.scrollToPosition(savedInstanceState.getInt("POSITION"));
        } else {
            loadPosters();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        movieAdapter.saveInstanceState(outState);
        int scrollPosition = gridLayoutManager.findFirstVisibleItemPosition();
        outState.putInt("POSITION", scrollPosition);
    }


    private void loadPosters() {
        if (loader == 0) {
            Bundle args = new Bundle();
            getSupportLoaderManager().restartLoader(LOADER_ID,args,this);
        }
    }

    private AlertDialog initSortingDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.sorting_value);
        builder.setItems(R.array.pref_sorting_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] criteria = getResources().getStringArray(R.array.pref_sorting_values);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MOVIE_SORTING, criteria[which]);
                editor.apply();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("CANCEL", "Canceled");
            }
        });
        return builder.create();
    }


    private void initSharedPreferences() {
        sharedPreferences = getApplicationContext().getSharedPreferences("movie_preferences", MODE_PRIVATE);
        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                actualCriterion = sharedPreferences.getString(key, getString(R.string.pref_sorting_popular));
                loader =0;
                movieAdapter.clear();
                loadPosters();
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        actualCriterion = sharedPreferences.getString(MOVIE_SORTING,MOVIE_BOOKMARKED);
    }

    @Override
    public void onClick(MovieInfo movieInfo) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(MovieInfo.EXTRA_MOVIE, movieInfo.toBundle());
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sorting_criteria) {
            if (alertDialog != null) {
                alertDialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<MovieInfo>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<MovieInfo>>(this) {
            ArrayList<MovieInfo> mData;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (actualCriterion.equals(MOVIE_BOOKMARKED)) {
                    //force refresh
                    movieAdapter.clear();
                    forceLoad();
                }
                else {
                    if (mData != null) {
                        deliverResult(mData);
                    } else {
                        if (loader == 0) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        errorTextView.setVisibility(View.INVISIBLE);
                        forceLoad();
                    }
                }
            }

            @Override
            public ArrayList<MovieInfo> loadInBackground() {

                NetworkUtils networker = new NetworkUtils();
                if (!(actualCriterion.equals(MOVIE_BOOKMARKED))) {
                    URL request = networker.buildMoviesUrl(actualCriterion);
                    try {
                        String JSONResponse = networker.getResponseFromHttpUrl(request);
                        ArrayList<MovieInfo> res =  JsonUtils.ParseMoviesJsonData(JSONResponse);
                        loader++;
                        return res;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                else{
                    Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);
                    if (cursor!=null){
                        ArrayList<MovieInfo> res = fetchMoviesFromCursor(cursor);
                        cursor.close();
                        return res;
                    }
                    return null;
                }

            }

            @Override
            public void deliverResult(ArrayList<MovieInfo> data) {
                mData = data;
                progressBar.setVisibility(View.INVISIBLE);
                super.deliverResult(data);
            }
        };
    }
    @Override
    public void onLoadFinished(Loader<ArrayList<MovieInfo>> loader, ArrayList<MovieInfo> movieInfos) {
        progressBar.setVisibility(View.INVISIBLE);
        if (movieInfos != null) {
            movieAdapter.addMovies(movieInfos);
            showPosters();
        } else {
            if (actualCriterion.equals(MOVIE_BOOKMARKED)) {
                showNoBookmarksMessage();
            }else {
                showErrorMessage();
            }
        }
    }
    @Override
    public void onLoaderReset(Loader<ArrayList<MovieInfo>> loader) {

    }

    private void showNoBookmarksMessage(){
        recyclerView.setVisibility(View.INVISIBLE);
        errorTextView.setVisibility(View.INVISIBLE);
        noBookmarksTextView.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        noBookmarksTextView.setVisibility(View.INVISIBLE);
        errorTextView.setVisibility(View.VISIBLE);
    }
    private void showPosters() {
        recyclerView.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.INVISIBLE);
        noBookmarksTextView.setVisibility(View.INVISIBLE);
    }


    private ArrayList<MovieInfo> fetchMoviesFromCursor(Cursor cursor){
        ArrayList<MovieInfo> result = new ArrayList<>();

        if (cursor.getCount()==0){
            return null;
        }
        if(cursor.moveToFirst()){
            do{
                MovieInfo movieInfo = new MovieInfo(
                        cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_POSTER_PATH)),
                        cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_AVG)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_RELEASE_DATE))
                );

                movieInfo.setPosterFromCursor(cursor);

                result.add(movieInfo);

            }while(cursor.moveToNext());

        }

        return result;
    }
}