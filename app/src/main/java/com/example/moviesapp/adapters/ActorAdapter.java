package com.example.moviesapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviesapp.R;
import com.example.moviesapp.entities.Actor;

import java.util.List;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ViewHolder> {
    private final Context context;
    private final List<Actor> actors;

    public ActorAdapter(List<Actor> actors, Context context) {
        this.actors = actors;
        this.context = context;
    }

    @NonNull
    @Override
    public ActorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getApplicationContext())
                .inflate(R.layout.item_actor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ActorAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(actors.get(position).getProfile_path())
                .into(holder.imgActor);
        holder.txtName.setText(actors.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return actors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgActor;
        private final TextView txtName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgActor = itemView.findViewById(R.id.imgActor);
            txtName = itemView.findViewById(R.id.txtName);
        }
    }
}