package com.lexoid.popularmovies.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lexoid.popularmovies.R;
import com.lexoid.popularmovies.data.FavoritesContract;
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
    public static final String SCROLL_Y = "scrollBarPosition";

    private Movie movie;

    private MoviesRepository moviesRepository = MoviesRepository.getInstance();
    private RecyclerView videoRv;
    private RecyclerView reviewRv;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            movie = extras.getParcelable(MOVIE_KEY);

            setTitle(movie.getOriginalTitle());

            scrollView = findViewById(R.id.scrollView2);
            TextView titleTv = findViewById(R.id.title_tv);
            ImageView posterIv = findViewById(R.id.poster_iv);
            TextView descriptionTv = findViewById(R.id.description_tv);
            TextView releaseTv = findViewById(R.id.release_tv);
            RatingBar ratingBar = findViewById(R.id.ratingBar);
            TextView ratingTv = findViewById(R.id.rating_tv);
            final ToggleButton favoriteTb = findViewById(R.id.favorite_tb);

            titleTv.setText(movie.getOriginalTitle());
            descriptionTv.setText(movie.getOverview());
            releaseTv.setText(movie.getReleaseDate());
            ratingBar.setRating(movie.getVoteAverage());
            ratingTv.setText(String.valueOf(movie.getVoteAverage()));
            Picasso.with(this)
                    .load(MoviesUtils.getFullImagePath(movie.getPosterPath()))
                    .into(posterIv);

            favoriteTb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkMovieAsFavorite(favoriteTb.isChecked());
                }
            });

            videoRv = findViewById(R.id.videos_rv);
            reviewRv = findViewById(R.id.reviews_rv);

            moviesRepository.getVideos(movie.getId(), this);
            moviesRepository.getReviews(movie.getId(), this);

            favoriteTb.setChecked(movieIsFavorite());

        } else {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int scrollY = scrollView.getScrollY();
        outState.putInt(SCROLL_Y, scrollY);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (scrollView != null && savedInstanceState != null){
            int scrollY = savedInstanceState.getInt(SCROLL_Y);
            scrollView.scrollTo(0, scrollY);
        }
    }

    private void checkMovieAsFavorite(boolean check){
        if (check){

            ContentValues values = new ContentValues();
            values.put(FavoritesContract.MovieEntry.MOVIE_ID_COLUMN, movie.getId());
            values.put(FavoritesContract.MovieEntry.ORIGINAL_TITLE, movie.getOriginalTitle());
            values.put(FavoritesContract.MovieEntry.POSTER_PATH, movie.getPosterPath());
            values.put(FavoritesContract.MovieEntry.OVERVIEW, movie.getOverview());
            values.put(FavoritesContract.MovieEntry.VOTE_AVERAGE, movie.getVoteAverage());
            values.put(FavoritesContract.MovieEntry.RELEASE_DATE, movie.getReleaseDate());

            Uri uriResult = getContentResolver().insert(FavoritesContract.MovieEntry.CONTENT_URI, values);
        } else {
            Uri deleteUri = FavoritesContract.MovieEntry.CONTENT_URI;
            String where = FavoritesContract.MovieEntry.MOVIE_ID_COLUMN + "=?";
            String[] whereArgs = {Integer.toString(movie.getId())};
            int deletedRows = getContentResolver().delete(deleteUri, where, whereArgs);
        }
    }

    private boolean movieIsFavorite(){
        String where = FavoritesContract.MovieEntry.MOVIE_ID_COLUMN + "=?";
        String[] whereArgs = {Integer.toString(movie.getId())};
        Cursor cursor = getContentResolver().query(FavoritesContract.MovieEntry.CONTENT_URI,
                null,
                where,
                whereArgs,
                null);
        boolean result = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return result;
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
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_video_intent, Toast.LENGTH_LONG).show();
        }
    }
}
