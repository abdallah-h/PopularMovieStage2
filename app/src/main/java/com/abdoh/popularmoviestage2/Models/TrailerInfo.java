package com.abdoh.popularmoviestage2.Models;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by abdoh.
 */

public class TrailerInfo {
    public String title;
    public String url;

    public TrailerInfo(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public static String arrayToString(ArrayList<TrailerInfo> trailers){
        String res = "";
        try {
            for (int i = 0; i < trailers.size(); i++) {
                res += trailers.get(i).title + "," + trailers.get(i).url;
                if (i < trailers.size() - 1) {
                    res += " -trailerSeparator- ";
                }
            }
        }catch (NullPointerException e){
            return "";
        }
        return res;
    }

    public static ArrayList<TrailerInfo> stringToArray(String string){
        String[] elements = string.split(" -trailerSeparator- ");

        ArrayList<TrailerInfo> res = new ArrayList<>();

        for (String element : elements) {
            try {
                String[] item = element.split(",");
                res.add(new TrailerInfo(item[0], item[1]));
            } catch (IndexOutOfBoundsException e) {
                Log.d("TRAILERS", e.toString());
            }
        }

        return res;
    }

}