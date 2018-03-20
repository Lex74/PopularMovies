package com.lexoid.popularmovies.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lexoid.popularmovies.R;
import com.lexoid.popularmovies.data.MoviesRepository;
import com.lexoid.popularmovies.data.models.Movie;
import com.lexoid.popularmovies.data.models.Review;
import com.lexoid.popularmovies.data.models.Video;
import com.lexoid.popularmovies.utils.MoviesUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements
        MoviesRepository.MoviesResultListener,
        VideoAdapter.VideoClickListener{
    public static final String MOVIE_KEY = "movie";

    private Movie movie;

    private MoviesRepository moviesRepository = MoviesRepository.getInstance(this);
    private RecyclerView videoRv;
    private RecyclerView reviewRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            movie = extras.getParcelable(MOVIE_KEY);

            setTitle(movie.getOriginalTitle());

            TextView titleTv = findViewById(R.id.title_tv);
            ImageView posterIv = findViewById(R.id.poster_iv);
            TextView descriptionTv = findViewById(R.id.description_tv);
            TextView releaseTv = findViewById(R.id.release_tv);
            RatingBar ratingBar = findViewById(R.id.ratingBar);
            TextView ratingTv = findViewById(R.id.rating_tv);

            titleTv.setText(movie.getOriginalTitle());
            descriptionTv.setText(movie.getOverview());
            releaseTv.setText(movie.getReleaseDate());
            ratingBar.setRating(movie.getVoteAverage());
            ratingTv.setText(String.valueOf(movie.getVoteAverage()));
            Picasso.with(this)
                    .load(MoviesUtils.getFullImagePath(movie.getPosterPath()))
                    .into(posterIv);

            videoRv = findViewById(R.id.videos_rv);
            reviewRv = findViewById(R.id.reviews_rv);

            moviesRepository.getVideos(movie.getId());
            moviesRepository.getReviews(movie.getId());
        } else {
            finish();
        }
    }

    @Override
    public void onGetMovies(ArrayList<Movie> moviesList) {

    }

    @Override
    public void onGetVideos(List<Video> videos) {
        VideoAdapter videoAdapter = new VideoAdapter(videos, this);
        videoRv.setAdapter(videoAdapter);
    }

    @Override
    public void onGetReviews(List<Review> reviews) {
        if (reviews != null) {
            ReviewAdapter reviewAdapter = new ReviewAdapter(reviews);
            reviewRv.setAdapter(reviewAdapter);
        }
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onVideoClick(String videoKey) {
        String videoUrl = MoviesUtils.getFullVideoPath(videoKey);
        Uri uri = Uri.parse(videoUrl);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
