package com.lexoid.popularmovies.data;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.lexoid.popularmovies.BuildConfig;
import com.lexoid.popularmovies.data.models.Movie;
import com.lexoid.popularmovies.data.models.Review;
import com.lexoid.popularmovies.data.models.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MoviesRepository {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private final String apiKey = BuildConfig.TMDB_API_KEY;

    private static final MoviesRepository INSTANCE = new MoviesRepository();

    private PopularMoviesEndpoint popularMoviesEndpoint;
    private TopRatedMoviesEndpoint topRatedMoviesEndpoint;
    private VideosEndpoint videosEndpoint;
    private ReviewsEndpoint reviewsEndpoint;
    private Retrofit retrofit;

    private MoviesRepository(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        popularMoviesEndpoint = retrofit.create(PopularMoviesEndpoint.class);
        topRatedMoviesEndpoint = retrofit.create(TopRatedMoviesEndpoint.class);
        videosEndpoint = retrofit.create(VideosEndpoint.class);
        reviewsEndpoint = retrofit.create(ReviewsEndpoint.class);
    }

    public static MoviesRepository getInstance(){
        return INSTANCE;
    }

    public void getPopularMovies(final MoviesResultListener moviesResultListener){
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

    public void getTopRatedMovies(final MoviesResultListener moviesResultListener) {
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

    public void getVideos(int movieId, final MoviesResultListener moviesResultListener){
        videosEndpoint.getVideos(movieId, apiKey).enqueue(new Callback<Video.VideoList>() {
            @Override
            public void onResponse(Call<Video.VideoList> call, Response<Video.VideoList> response) {
                moviesResultListener.onGetVideos(response.body().getVideos());
            }

            @Override
            public void onFailure(Call<Video.VideoList> call, Throwable t) {
                moviesResultListener.onError(t);
            }
        });
    }

    public void getReviews(int movieId, final MoviesResultListener moviesResultListener){
        reviewsEndpoint.getReviews(movieId, apiKey).enqueue(new Callback<Review.ReviewList>() {
            @Override
            public void onResponse(Call<Review.ReviewList> call, Response<Review.ReviewList> response) {
                moviesResultListener.onGetReviews(response.body().getReviews());
            }

            @Override
            public void onFailure(Call<Review.ReviewList> call, Throwable t) {
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

    public interface VideosEndpoint{
        @GET("movie/{movie_id}/videos")
        Call<Video.VideoList> getVideos(@Path("movie_id") int movieId, @Query("api_key") String apiKey);
    }

    public interface ReviewsEndpoint{
        @GET("movie/{movie_id}/reviews")
        Call<Review.ReviewList> getReviews(@Path("movie_id") int movieId, @Query("api_key") String apiKey);
    }

    public interface MoviesResultListener {
        void onGetMovies(ArrayList<Movie> moviesList);
        void onGetVideos(List<Video> videos);
        void onGetReviews(List<Review> reviews);
        void onError(Throwable t);
    }
}
