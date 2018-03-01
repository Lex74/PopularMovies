package com.lexoid.popularmovies.utils;

public class MoviesUtils {
    private static final String IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185";

    public static String getFullImagePath(String endPath){
        return IMAGE_BASE_PATH + endPath;
    }
}
