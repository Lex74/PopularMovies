package com.lexoid.popularmovies.data.models;

import java.util.List;

public class Video {
    private String id;
    private String key;
    private String name;

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public class VideoList{
        private List<Video> results;

        public List<Video> getVideos() {
            return results;
        }
    }
}
