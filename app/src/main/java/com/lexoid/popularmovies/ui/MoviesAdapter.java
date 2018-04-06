package com.lexoid.popularmovies.ui;

import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lexoid.popularmovies.R;
import com.lexoid.popularmovies.data.models.Movie;
import com.lexoid.popularmovies.utils.MoviesUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private List<Movie> moviesList;
    private OnPosterClickListener onPosterClickListener;

    public MoviesAdapter(List<Movie> moviesList, OnPosterClickListener onPosterClickListener){
        this.moviesList = moviesList;
        this.onPosterClickListener = onPosterClickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bindData(moviesList.get(position).getPosterPath());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{
        private ImageView posterIv;

        MovieViewHolder(View itemView) {
            super(itemView);
            posterIv = itemView.findViewById(R.id.poster_iv);
        }

        void bindData(String imagePath){
            final String fullImagePath = MoviesUtils.getFullImagePath(imagePath);
            Picasso.with(posterIv.getContext())
                    .load(fullImagePath)
                    .into(posterIv);

            posterIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPosterClickListener.onPosterClick(moviesList.get(getAdapterPosition()), posterIv);
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                posterIv.setTransitionName(posterIv.getResources().getString(R.string.transition_poster_name));
            }
        }
    }

    public interface OnPosterClickListener{
        void onPosterClick(Movie movie, View sharedElement);
    }
}
