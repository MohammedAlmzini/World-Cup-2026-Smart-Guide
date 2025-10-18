package com.ahmmedalmzini783.wcguide.ui.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;
    private OnNewsItemClickListener listener;

    public interface OnNewsItemClickListener {
        void onNewsItemClick(NewsItem newsItem);
    }

    public NewsAdapter(List<NewsItem> newsList, OnNewsItemClickListener listener) {
        this.newsList = newsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);
        holder.bind(newsItem);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        private ImageView newsItemImage;
        private TextView newsItemTitle, newsItemDescription;
        private TextView newsItemCategory, newsItemDate;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsItemImage = itemView.findViewById(R.id.news_item_image);
            newsItemTitle = itemView.findViewById(R.id.news_item_title);
            newsItemDescription = itemView.findViewById(R.id.news_item_description);
            newsItemCategory = itemView.findViewById(R.id.news_item_category);
            newsItemDate = itemView.findViewById(R.id.news_item_date);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNewsItemClick(newsList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(NewsItem newsItem) {
            newsItemTitle.setText(newsItem.getTitle());
            newsItemDescription.setText(newsItem.getDescription());
            newsItemCategory.setText(newsItem.getCategory());

            // Format date
            if (newsItem.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                newsItemDate.setText(sdf.format(newsItem.getCreatedAt()));
            }

            // Load image
            if (newsItem.getImageUrl() != null && !newsItem.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(newsItem.getImageUrl())
                    .placeholder(R.drawable.ic_news)
                    .error(R.drawable.ic_news)
                    .into(newsItemImage);
            }
        }
    }
}
