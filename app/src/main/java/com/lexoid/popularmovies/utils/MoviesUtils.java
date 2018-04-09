package com.lexoid.popularmovies.utils;

public class MoviesUtils {
    private static final String IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w342";
    private static final String VIDEO_BASE_PATH = "http://youtube.com/watch?v=";
    private static final String VIDEO_THUMB_BASE_PATH = "http://img.youtube.com/vi/";

    public static String getFullImagePath(String endPath){
        return IMAGE_BASE_PATH + endPath;
    }

    public static String getFullVideoPath (String endPath) {
        return VIDEO_BASE_PATH + endPath;
    }

    public static String getVideoThumbPath (String endPath) {
        return VIDEO_THUMB_BASE_PATH + endPath + "/0.jpg";
    }
}
