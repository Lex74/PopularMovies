package com.lexoid.popularmovies.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lexoid.popularmovies.R;
import com.lexoid.popularmovies.data.MoviesRepository;
import com.lexoid.popularmovies.data.models.Movie;

import java.util.ArrayList;

import static com.lexoid.popularmovies.ui.DetailActivity.MOVIE_KEY;

public class MainActivity extends AppCompatActivity implements
        MoviesRepository.MoviesResultListener,
        MoviesAdapter.OnPosterClickListener{

    private static final String MOVIES_LIST_KEY = "moviesList";

    private static final int SORT_TYPE_POPULAR = 0;
    private static final int SORT_TYPE_RATED = 1;
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

        moviesRepository = new MoviesRepository(this);

        recyclerView = findViewById(R.id.movies_recycler);
        progressBar = findViewById(R.id.progressBar);
        refreshButton = findViewById(R.id.refresh_button);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoviesFromNet();
            }
        });

        if (savedInstanceState != null){
            sortType = savedInstanceState.getInt(SORT_TYPE_KEY);
            loadMoviesFromSavedState(savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (moviesList == null)
            loadMoviesFromNet();
    }

    private void loadMoviesFromNet(){
        if (sortType == SORT_TYPE_POPULAR) {
            moviesRepository.getPopularMovies();
        } else {
            moviesRepository.getTopRatedMovies();
        }
        progressBar.setVisibility(View.VISIBLE);
        refreshButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void loadMoviesFromSavedState(Bundle bundle){
        moviesList = bundle.getParcelableArrayList(MOVIES_LIST_KEY);

        showMoviesList();
    }

    @Override
    public void onGetMovies(ArrayList<Movie> moviesList) {
        this.moviesList = moviesList;
        showMoviesList();
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
    public void onPosterClick(Movie movie) {
        Bundle extraData = new Bundle();
        extraData.putParcelable(MOVIE_KEY, movie);

        startDetailActivity(extraData);
    }

    private void startDetailActivity(Bundle extraData){
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtras(extraData);
        startActivity(intent);
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
        if (item.getItemId() == R.id.action_sort){
            switchSortType(item);
            return true;
        }
        return false;
    }

    private void switchSortType(MenuItem item){
        if (sortType == SORT_TYPE_POPULAR){
            sortType = SORT_TYPE_RATED;

            item.setIcon(R.drawable.ic_action_star);
            item.setTitle(R.string.sort_by_rating);
        } else {
            sortType = SORT_TYPE_POPULAR;

            item.setIcon(R.drawable.ic_action_people);
            item.setTitle(R.string.sort_by_popular);
        }

        loadMoviesFromNet();
    }
}
