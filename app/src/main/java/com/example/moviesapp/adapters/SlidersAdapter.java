package com.example.moviesapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviesapp.R;
import com.example.moviesapp.activities.DetailActivity;
import com.example.moviesapp.entities.SliderItems;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SlidersAdapter extends RecyclerView.Adapter<SlidersAdapter.SliderViewHolder> {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private final List<SliderItems> sliderItems;
    private final ViewPager2 viewPager2;
    private final Runnable runnable = new Runnable() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };

    public SlidersAdapter(List<SliderItems> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SlidersAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_viewholder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SlidersAdapter.SliderViewHolder holder, int position) {
        holder.setData(sliderItems.get(position));
        if (position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }
        holder.itemView.setOnClickListener(v -> {
            int movieId = sliderItems.get(position).getId();
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("movieId", movieId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameTxt;
        private final TextView yearTxt;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            yearTxt = itemView.findViewById(R.id.yearTxt);
        }

        public void setData(SliderItems sliderItems) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(),
                    new RoundedCorners(60));
            Glide.with(context)
                    .load(sliderItems.getBackdrop_path())
                    .apply(requestOptions)
                    .into(imageView);
            nameTxt.setText(sliderItems.getTitle());
            String releaseDate = sliderItems.getRelease_date();
            String yearString = "";
            if (releaseDate != null && !releaseDate.isEmpty()) {
                try {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = inputFormat.parse(releaseDate);
                    if (date != null) {
                        yearString = outputFormat.format(date);
                    }
                } catch (Exception e) {
                    Log.e("DATE_PARSING_ERROR", "Error parsing date: " + releaseDate, e);
                }
            }
            yearTxt.setText(yearString);
        }
    }
}
