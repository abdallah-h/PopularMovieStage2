package com.abdoh.popularmoviestage2;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by abdoh.
 */


public class NetworkUtils {

    public URL buildMoviesUrl(String sorting) {
        String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
        // put your api key here
        String MY_API_KEY = "";
        String API_PARAM_KEY = "api_key";
        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(sorting)
                .appendQueryParameter(API_PARAM_KEY, MY_API_KEY)
                .build();


        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public URL buildTrailersUrl(long id){
        String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
        String API_PARAM_KEY = "api_key";
        // put your api key here
        String MY_API_KEY = "";
        String API_TRAILERS_PATH = "videos";

        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(Long.toString(id))
                .appendPath(API_TRAILERS_PATH)
                .appendQueryParameter(API_PARAM_KEY, MY_API_KEY)
                .build();


        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    public URL buildReviewsUrl(long id){
        String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
        String API_PARAM_KEY = "api_key";
        // put your api key here
        String MY_API_KEY = "";
        String API_REVIEWS_PATH = "reviews";

        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(Long.toString(id))
                .appendPath(API_REVIEWS_PATH)
                .appendQueryParameter(API_PARAM_KEY, MY_API_KEY)
                .build();


        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}