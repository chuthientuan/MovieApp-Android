package com.example.moviesapp.adapters;

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
import com.example.moviesapp.entities.Film;
import com.example.moviesapp.fragment.ExplorerFragment;

import java.util.List;

public class UpcomingMovieAdapter extends RecyclerView.Adapter<UpcomingMovieAdapter.ViewHolder> {
    private final List<Film> films;
    private final ExplorerFragment context;

    public UpcomingMovieAdapter(List<Film> films, ExplorerFragment context) {
        this.films = films;
        this.context = context;
    }

    @NonNull
    @Override
    public UpcomingMovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getContext()).inflate(R.layout.item_film_ex, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingMovieAdapter.ViewHolder holder, int position) {
        holder.setData(films.get(position));
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView txtName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            txtName = itemView.findViewById(R.id.txtName);
        }

        public void setData(Film film) {
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
