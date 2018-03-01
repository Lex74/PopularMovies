package com.lexoid.popularmovies.data;

import com.lexoid.popularmovies.BuildConfig;
import com.lexoid.popularmovies.data.models.Movie;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MoviesRepository {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private PopularMoviesEndpoint popularMoviesEndpoint;
    private TopRatedMoviesEndpoint topRatedMoviesEndpoint;
    private MoviesResultListener moviesResultListener;

    public MoviesRepository(MoviesResultListener moviesResultListener){
        this.moviesResultListener = moviesResultListener;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        popularMoviesEndpoint = retrofit.create(PopularMoviesEndpoint.class);
        topRatedMoviesEndpoint = retrofit.create(TopRatedMoviesEndpoint.class);
    }

    public void getPopularMovies(){
        String apiKey = BuildConfig.TMDB_API_KEY;

        popularMoviesEndpoint.getPopularMovies(apiKey).enqueue(new Callback<Movie.MoviesList>() {
            @Override
            public void onResponse(Call<Movie.MoviesList> call, Response<Movie.MoviesList> response) {
                moviesResultListener.onGetMovies(response.body().getResults());
            }

            @Override
            public void onFailure(Call<Movie.MoviesList> call, Throwable t) {
                moviesResultListener.onError(t);
            }
        });
    }

    public void getTopRatedMovies() {
        String apiKey = BuildConfig.TMDB_API_KEY;

        topRatedMoviesEndpoint.getTopRatedMovies(apiKey).enqueue(new Callback<Movie.MoviesList>() {
            @Override
            public void onResponse(Call<Movie.MoviesList> call, Response<Movie.MoviesList> response) {
                moviesResultListener.onGetMovies(response.body().getResults());
            }

            @Override
            public void onFailure(Call<Movie.MoviesList> call, Throwable t) {
                moviesResultListener.onError(t);
            }
        });
    }

    public interface PopularMoviesEndpoint{
        @GET("movie/popular/")
        Call<Movie.MoviesList> getPopularMovies(@Query("api_key") String apikey);
    }

    public interface TopRatedMoviesEndpoint{
        @GET("movie/top_rated/")
        Call<Movie.MoviesList> getTopRatedMovies(@Query("api_key") String apikey);
    }

    public interface MoviesResultListener {
        void onGetMovies(ArrayList<Movie> moviesList);
        void onError(Throwable t);
    }
}
