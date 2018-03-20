package com.lexoid.popularmovies.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lexoid.popularmovies.R;
import com.lexoid.popularmovies.data.models.Video;
import com.lexoid.popularmovies.utils.MoviesUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<Video> videos;
    private VideoClickListener videoClickListener;

    public VideoAdapter(List<Video> videos, VideoClickListener videoClickListener) {
        this.videos = videos;
        this.videoClickListener = videoClickListener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.bind(videos.get(position));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{
        private TextView videoName;
        private ImageView videoThumb;
        private View itemView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoName = itemView.findViewById(R.id.video_name);
            videoThumb = itemView.findViewById(R.id.video_thumb);
            this.itemView = itemView;
        }

        public void bind (final Video video){
            videoName.setText(video.getName());
            String videoThumbUrl = MoviesUtils.getVideoThumbPath(video.getKey());
            Picasso
                    .with(videoThumb.getContext())
                    .load(videoThumbUrl)
                    .into(videoThumb);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoClickListener.onVideoClick(video.getKey());
                }
            });
        }
    }

    interface VideoClickListener{
        void onVideoClick(String videoKey);
    }
}
