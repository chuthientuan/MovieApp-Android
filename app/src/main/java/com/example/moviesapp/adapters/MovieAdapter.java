package com.example.moviesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviesapp.R;
import com.example.moviesapp.activities.DetailActivity;
import com.example.moviesapp.entities.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private final List<Movie> movies;
    private final Context context;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Movie movie);
    }

    public MovieAdapter(List<Movie> movies, Context context) {
        this(movies, context, null);
    }

    public MovieAdapter(List<Movie> movies, Context context, OnFavoriteClickListener listener) {
        this.movies = movies;
        this.context = context;
        this.favoriteClickListener = listener;
    }

    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getApplicationContext())
                .inflate(R.layout.item_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.setData(movie);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), DetailActivity.class);
            intent.putExtra("movieId", movie.getId());
            context.startActivity(intent);
        });

        if (favoriteClickListener != null) {
            holder.btnFavorite.setVisibility(View.VISIBLE);
            holder.btnFavorite.setOnClickListener(v ->
                    favoriteClickListener.onFavoriteClick(movie));
        } else {
            holder.btnFavorite.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView txtName;
        private final ImageView btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            txtName = itemView.findViewById(R.id.txtName);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }

        public void setData(Movie movie) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(),
                    new RoundedCorners(30));
            Glide.with(context)
                    .load(movie.getPoster_path())
                    .apply(requestOptions)
                    .into(image);
            txtName.setText(movie.getTitle());

            // Update favorite icon
            btnFavorite.setImageResource(movie.isFavorite() ?
                    R.drawable.bookmark_yellow : R.drawable.bookmark_white);
        }
    }
}
