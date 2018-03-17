package com.abdoh.popularmoviestage2.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.abdoh.popularmoviestage2.R;
import com.abdoh.popularmoviestage2.database.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by abdoh.
 */

public class MovieInfo implements Parcelable

{
    public static final String EXTRA_MOVIE = "com.abdoh.popularmoviestage2.EXTRA_MOVIE";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE ="title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH ="poster_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";

    public final long id;
    public final String title;
    public final String overview;
    private final String poster_path;
    public final double vote_average;
    public final String release_date;
    private ArrayList<TrailerInfo> trailers;
    private ArrayList<ReviewInfo> reviews;
    private Bitmap poster;

    public MovieInfo(long id, String title, String overview, String poster_path, double vote_average, String release_date)
    {
        this.id=id;
        this.title=title;
        this.overview=overview;
        this.poster_path=poster_path;
        this.vote_average=vote_average;
        this.release_date=release_date;
        this.trailers = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }


    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }
    public void setPosterFromCursor(Cursor cursor){
        byte[] bytes = cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_POSTER));
        ByteArrayInputStream posterStream = new ByteArrayInputStream(bytes);
        this.poster = BitmapFactory.decodeStream(posterStream);
    }

    public void setTrailers(ArrayList<TrailerInfo> trailers) {
        this.trailers = trailers;
    }

    public void setReviews(ArrayList<ReviewInfo> reviews) {
        this.reviews = reviews;
    }


    public MovieInfo(Bundle bundle)
    {
        this(bundle.getLong(KEY_ID),
                                bundle.getString(KEY_TITLE),
                                bundle.getString(KEY_OVERVIEW),
                                bundle.getString(KEY_POSTER_PATH),
                                bundle.getDouble(KEY_VOTE_AVERAGE),
                                bundle.getString(KEY_RELEASE_DATE));

    }


    public MovieInfo(Parcel in) {
        id = in.readLong();
        title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        vote_average = in.readDouble();
        release_date = in.readString();
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_ID, id);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_OVERVIEW, overview);
        bundle.putString(KEY_POSTER_PATH, poster_path);
        bundle.putDouble(KEY_VOTE_AVERAGE, vote_average);
        bundle.putString(KEY_RELEASE_DATE,release_date);
        return bundle;
    }

    public static MovieInfo getMovieFromJson(JSONObject jsonObject) throws JSONException
    {
        return new MovieInfo(jsonObject.getLong(KEY_ID),
                jsonObject.getString(KEY_TITLE),
                jsonObject.getString(KEY_OVERVIEW),
                jsonObject.getString(KEY_POSTER_PATH),
                jsonObject.getDouble(KEY_VOTE_AVERAGE),
                jsonObject.getString(KEY_RELEASE_DATE));
    }

    //returns the uri needed for Picasso.

    public Uri getPosterUri(String size)
    {
        final String BASE_URL = "http://image.tmdb.org/t/p";

        return Uri.parse(BASE_URL).buildUpon().appendPath(size).appendEncodedPath(poster_path).build();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(poster_path);
        dest.writeDouble(vote_average);
        dest.writeString(release_date);
    }

    public boolean saveToBookmarks(Context context){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.MOVIE_ID, this.id);
        contentValues.put(MovieContract.MovieEntry.MOVIE_TITLE, this.title);
        contentValues.put(MovieContract.MovieEntry.MOVIE_OVERVIEW, this.overview);
        contentValues.put(MovieContract.MovieEntry.MOVIE_POSTER_PATH, this.poster_path);
        contentValues.put(MovieContract.MovieEntry.MOVIE_AVG, this.vote_average);
        contentValues.put(MovieContract.MovieEntry.MOVIE_RELEASE_DATE, this.release_date);
        contentValues.put(MovieContract.MovieEntry.MOVIE_TRAILERS, TrailerInfo.arrayToString(trailers));
        contentValues.put(MovieContract.MovieEntry.MOVIE_REVIEWS, ReviewInfo.arrayToString(reviews));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(this.poster != null) {
            this.poster.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            contentValues.put(MovieContract.MovieEntry.MOVIE_POSTER, bytes);
        }else if (this.poster == null){
            contentValues.put(MovieContract.MovieEntry.MOVIE_POSTER, String.valueOf(this.poster));
        }

        if (context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,contentValues)!=null){
            Toast.makeText(context, R.string.bookmark_added, Toast.LENGTH_SHORT).show();
            return true;
        }else{
            Toast.makeText(context, R.string.bookmark_insert_error, Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public boolean removeFromBookmarks(Context context){
        long deletedRows = context.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_ID + "=?",new String[]{Long.toString(this.id)});
        if (deletedRows>0){
            Toast.makeText(context, R.string.bookmark_deleted, Toast.LENGTH_SHORT).show();
            return true;
        }else {
            Toast.makeText(context, R.string.bookmark_delete_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean isBookmarked(Context context){
        Cursor cursor = context.getContentResolver()
                .query(MovieContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieContract.MovieEntry.MOVIE_ID},
                        MovieContract.MovieEntry.MOVIE_ID + "=?",
                        new String[]{Long.toString(this.id)},null);
        if (cursor!=null) {
            boolean bookmarked = cursor.getCount() > 0;
            cursor.close();
            return bookmarked;
        }
        return false;
    }

}
