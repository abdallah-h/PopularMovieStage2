package com.abdoh.popularmoviestage2.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by abdoh.
 */

public class MovieContract {
    public static String CONTENT_AUTHORITY = "com.abdoh.popularmoviestage2";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static String PATH_MOVIE = "movies";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String TABLE_NAME = "BookmarkedMovies";
        public static final String MOVIE_ID = "id";
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_OVERVIEW = "overview";
        public static final String MOVIE_POSTER= "poster";
        public static final String MOVIE_POSTER_PATH = "poster_path";
        public static final String MOVIE_AVG = "voteAverage";
        public static final String MOVIE_RELEASE_DATE = "releaseDate";
        public static final String MOVIE_TRAILERS = "trailers";
        public static final String MOVIE_REVIEWS = "reviews";



        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(BASE_CONTENT_URI,id);
        }

    }
}
