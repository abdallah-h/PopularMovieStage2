package com.abdoh.popularmoviestage2.Models;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by abdoh.
 */

public class ReviewInfo {
    public String author;
    public String content;

    public ReviewInfo(String author, String content) {
        this.author = author;
        this.content = content;
    }
    public static String arrayToString(ArrayList<ReviewInfo> reviews){
        String res = "";
        try {
            for (int i = 0; i < reviews.size(); i++) {
                res += reviews.get(i).author + ",reviewSeparator," + reviews.get(i).content;
                if (i < reviews.size() - 1) {
                    res += " -reviewSeparator- ";
                }
            }
        }catch (NullPointerException e){
            return "";
        }
        return res;
    }

    public static ArrayList<ReviewInfo> stringToArray(String string){
        String[] elements = string.split(" -reviewSeparator- ");
        ArrayList<ReviewInfo> res = new ArrayList<>();

        for (String element : elements) {
            String[] item = element.split(",reviewSeparator,");
            try{
                res.add(new ReviewInfo(item[0], item[1]));
            }catch (IndexOutOfBoundsException e){
                Log.d("REVIEWS",e.toString());
            }
        }
        return res;
    }
}