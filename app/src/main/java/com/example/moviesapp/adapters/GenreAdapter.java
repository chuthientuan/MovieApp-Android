package com.example.moviesapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.R;
import com.example.moviesapp.entities.Genre;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    private final List<Genre> genres;
    private final Context context;

    public GenreAdapter(List<Genre> genres, Context context) {
        this.genres = genres;
        this.context = context;
    }

    @NonNull
    @Override
    public GenreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getApplicationContext())
                .inflate(R.layout.item_genre, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.ViewHolder holder, int position) {
        holder.txtGenre.setText(genres.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtGenre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGenre = itemView.findViewById(R.id.txtGenre);
        }
    }
}
