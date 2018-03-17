package com.abdoh.popularmoviestage2;

import com.abdoh.popularmoviestage2.Models.MovieInfo;
import com.abdoh.popularmoviestage2.Models.ReviewInfo;
import com.abdoh.popularmoviestage2.Models.TrailerInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by abdoh on 2018-03-17.
 */

public class JsonUtils {

    static ArrayList<MovieInfo> ParseMoviesJsonData(String JsonString) throws JSONException {

        JSONObject obj = new JSONObject(JsonString);
        JSONArray resultsArray = obj.getJSONArray("results");
        ArrayList<MovieInfo> result = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            MovieInfo resMovie = MovieInfo.getMovieFromJson(resultsArray.getJSONObject(i));
            result.add(resMovie);
        }
        return result;
    }

    static ArrayList<TrailerInfo> ParseTrailersJsonData(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
        JSONArray resultsArray = obj.getJSONArray("results");
        ArrayList<TrailerInfo> result = new ArrayList<>();

        for (int i = 0; i< resultsArray.length(); i++){
            JSONObject trailerObject = resultsArray.getJSONObject(i);
            String site = trailerObject.getString("site");
            if (site.equals("YouTube")){
                String url = "https://www.youtube.com/watch?v="+trailerObject.getString("key");
                result.add(new TrailerInfo(trailerObject.getString("name"),url));
            }
        }
        return result;
    }

    static ArrayList<ReviewInfo> ParseReviewsJsonData(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
        JSONArray resultsArray = obj.getJSONArray("results");
        ArrayList<ReviewInfo> result = new ArrayList<>();

        for (int i = 0; i< resultsArray.length(); i++){
            JSONObject trailerObject = resultsArray.getJSONObject(i);
            result.add(new ReviewInfo(trailerObject.getString("author"),trailerObject.getString("content")));
        }
        return result;
    }
}
