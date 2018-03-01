package com.lexoid.popularmovies.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lexoid.popularmovies.R;
import com.lexoid.popularmovies.data.models.Movie;
import com.lexoid.popularmovies.utils.MoviesUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    public static final String MOVIE_KEY = "movie";

    private Movie movie;

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
        }
    }
}
