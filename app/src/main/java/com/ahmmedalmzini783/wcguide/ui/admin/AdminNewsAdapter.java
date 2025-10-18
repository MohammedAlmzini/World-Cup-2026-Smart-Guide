package com.ahmmedalmzini783.wcguide.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.news.NewsItem;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminNewsAdapter extends RecyclerView.Adapter<AdminNewsAdapter.AdminNewsViewHolder> {

    private List<NewsItem> newsList;
    private OnNewsItemClickListener listener;
    private OnEditNewsClickListener editListener;
    private OnDeleteNewsClickListener deleteListener;

    public interface OnNewsItemClickListener {
        void onNewsItemClick(NewsItem newsItem);
    }

    public interface OnEditNewsClickListener {
        void onEditNewsClick(NewsItem newsItem);
    }

    public interface OnDeleteNewsClickListener {
        void onDeleteNewsClick(NewsItem newsItem);
    }

    public AdminNewsAdapter(List<NewsItem> newsList, OnNewsItemClickListener listener, 
                           OnEditNewsClickListener editListener, OnDeleteNewsClickListener deleteListener) {
        this.newsList = newsList;
        this.listener = listener;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public AdminNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new AdminNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminNewsViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);
        holder.bind(newsItem);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class AdminNewsViewHolder extends RecyclerView.ViewHolder {
        private ImageView newsItemImage;
        private TextView newsItemTitle, newsItemDescription;
        private TextView newsItemCategory, newsItemDate;
        private LinearLayout adminActionsLayout;
        private ImageView btnEditNews, btnDeleteNews;

        public AdminNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsItemImage = itemView.findViewById(R.id.news_item_image);
            newsItemTitle = itemView.findViewById(R.id.news_item_title);
            newsItemDescription = itemView.findViewById(R.id.news_item_description);
            newsItemCategory = itemView.findViewById(R.id.news_item_category);
            newsItemDate = itemView.findViewById(R.id.news_item_date);
            adminActionsLayout = itemView.findViewById(R.id.admin_actions_layout);
            btnEditNews = itemView.findViewById(R.id.btn_edit_news);
            btnDeleteNews = itemView.findViewById(R.id.btn_delete_news);

            // Show admin actions
            adminActionsLayout.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNewsItemClick(newsList.get(getAdapterPosition()));
                }
            });

            btnEditNews.setOnClickListener(v -> {
                if (editListener != null) {
                    editListener.onEditNewsClick(newsList.get(getAdapterPosition()));
                }
            });

            btnDeleteNews.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteNewsClick(newsList.get(getAdapterPosition()));
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
