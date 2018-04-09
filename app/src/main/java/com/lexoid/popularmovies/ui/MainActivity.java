package com.lexoid.popularmovies.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.lexoid.popularmovies.R;
import com.lexoid.popularmovies.data.FavoritesContract;
import com.lexoid.popularmovies.data.MoviesRepository;
import com.lexoid.popularmovies.data.models.Movie;
import com.lexoid.popularmovies.data.models.Review;
import com.lexoid.popularmovies.data.models.Video;

import java.util.ArrayList;
import java.util.List;

import static com.lexoid.popularmovies.ui.DetailActivity.MOVIE_KEY;

public class MainActivity extends AppCompatActivity implements
        MoviesRepository.MoviesResultListener,
        MoviesAdapter.OnPosterClickListener{

    private static final String MOVIES_LIST_KEY = "moviesList";

    private static final int SORT_TYPE_POPULAR = 0;
    private static final int SORT_TYPE_RATED = 1;
    private static final int SORT_TYPE_FAVORITES = 2;
    private static final String SORT_TYPE_KEY = "sort_type";

    private int sortType = SORT_TYPE_POPULAR;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button refreshButton;
    private MoviesRepository moviesRepository;

    private ArrayList<Movie> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesRepository = MoviesRepository.getInstance();

        recyclerView = findViewById(R.id.movies_recycler);
        progressBar = findViewById(R.id.progressBar);
        refreshButton = findViewById(R.id.refresh_button);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMovies();
            }
        });

        sortSpinnerInit();

        if (savedInstanceState != null){
            sortType = savedInstanceState.getInt(SORT_TYPE_KEY);
            loadMoviesFromSavedState(savedInstanceState);
        }
    }

    private void sortSpinnerInit(){
        Spinner sortSpinner = findViewById(R.id.sort_spinner);

        String[] sortByArray = getResources().getStringArray(R.array.sort_by_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortByArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortSpinner.setAdapter(adapter);

        sortSpinner.setSelection(sortType);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (sortType != position) {
                    sortType = position;
                    loadMovies();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (moviesList == null)
            loadMovies();
    }

    private void loadMovies(){
        progressBar.setVisibility(View.VISIBLE);
        refreshButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        switch (sortType){
            case SORT_TYPE_POPULAR:
                moviesRepository.getPopularMovies(this);
                break;
            case SORT_TYPE_RATED:
                moviesRepository.getTopRatedMovies(this);
                break;
            case SORT_TYPE_FAVORITES:
                getFavoriteMovies();
                break;
            default:
                throw new UnsupportedOperationException("Unknown sort type");
        }
    }

    private void loadMoviesFromSavedState(Bundle bundle){
        moviesList = bundle.getParcelableArrayList(MOVIES_LIST_KEY);

        showMoviesList();
    }

    private void getFavoriteMovies(){
        Cursor cursor = getContentResolver().query(FavoritesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        ArrayList<Movie> movies = new ArrayList<>();

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(FavoritesContract.MovieEntry.MOVIE_ID_COLUMN));
                    String title = cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.ORIGINAL_TITLE));
                    String posterPath = cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.POSTER_PATH));
                    String overview = cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.OVERVIEW));
                    float voteAverage = cursor.getFloat(cursor.getColumnIndex(FavoritesContract.MovieEntry.VOTE_AVERAGE));
                    String releaseDate = cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.RELEASE_DATE));

                    Movie movie = new Movie(id, title, posterPath, overview, voteAverage, releaseDate);

                    movies.add(movie);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        onGetMovies(movies);
    }

    @Override
    public void onGetMovies(ArrayList<Movie> moviesList) {
        this.moviesList = moviesList;
        showMoviesList();
    }

    @Override
    public void onGetVideos(List<Video> videos) {

    }

    @Override
    public void onGetReviews(List<Review> reviews) {

    }

    private void showMoviesList() {
        MoviesAdapter moviesAdapter = new MoviesAdapter(moviesList, this);
        recyclerView.setAdapter(moviesAdapter);

        showRecyclerView();
    }

    private void showRecyclerView(){
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        refreshButton.setVisibility(View.GONE);
    }

    private void showRefreshButton(){
        refreshButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
        showRefreshButton();
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPosterClick(Movie movie, View sharedElement) {
        Bundle extraData = new Bundle();
        extraData.putParcelable(MOVIE_KEY, movie);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                sharedElement,
                ViewCompat.getTransitionName(sharedElement));

        startDetailActivity(extraData, options.toBundle());
    }

    private void startDetailActivity(Bundle extraData, Bundle options){
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtras(extraData);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivity(intent, options);
        } else {
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_LIST_KEY, moviesList);
        outState.putInt(SORT_TYPE_KEY, sortType);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
