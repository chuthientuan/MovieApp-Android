package com.example.moviesapp.adapters;

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
import com.example.moviesapp.fragment.ExplorerFragment;

import java.util.List;

public class FetchMoviesAdapter extends RecyclerView.Adapter<FetchMoviesAdapter.ViewHolder> {
    private final List<Movie> movies;
    private final ExplorerFragment context;

    public FetchMoviesAdapter(List<Movie> movies, ExplorerFragment context) {
        this.movies = movies;
        this.context = context;
    }

    @NonNull
    @Override
    public FetchMoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getContext()).inflate(R.layout.item_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FetchMoviesAdapter.ViewHolder holder, int position) {
        holder.setData(movies.get(position));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context.getContext(), DetailActivity.class);
            intent.putExtra("movieId", movies.get(position).getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView txtName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            txtName = itemView.findViewById(R.id.txtName);
        }

        public void setData(Movie film) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(),
                    new RoundedCorners(30));
            Glide.with(context)
                    .load(film.getPoster_path())
                    .apply(requestOptions)
                    .into(image);
            txtName.setText(film.getTitle());
        }
    }
}
