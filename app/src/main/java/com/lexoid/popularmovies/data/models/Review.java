package com.lexoid.popularmovies.data.models;

import java.util.List;

public class Review {
    private String id;
    private String author;
    private String content;
    private String url;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public class ReviewList{
        private List<Review> results;

        public List<Review> getReviews() {
            return results;
        }
    }
}
