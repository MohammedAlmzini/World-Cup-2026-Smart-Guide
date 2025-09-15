package com.ahmmedalmzini783.wcguide.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.ReviewImage;
import com.ahmmedalmzini783.wcguide.util.ImageLoader;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ReviewImageAdapter extends RecyclerView.Adapter<ReviewImageAdapter.ImageViewHolder> {

    private List<ReviewImage> images;
    private OnImageClickListener listener;

    public interface OnImageClickListener {
        void onDeleteClick(ReviewImage image);
        void onEditClick(ReviewImage image);
    }

    public ReviewImageAdapter(List<ReviewImage> images, OnImageClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ReviewImage image = images.get(position);
        holder.bind(image, listener);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void updateImages(List<ReviewImage> newImages) {
        this.images.clear();
        if (newImages != null) {
            this.images.addAll(newImages);
        }
        notifyDataSetChanged();
    }

    public void addImage(ReviewImage image) {
        this.images.add(image);
        notifyItemInserted(images.size() - 1);
    }

    public void removeImage(int position) {
        if (position >= 0 && position < images.size()) {
            this.images.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, images.size());
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textImageName;
        private TextView textImageDescription;
        private TextView textImageUrl;
        private MaterialButton btnEdit;
        private MaterialButton btnDelete;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textImageName = itemView.findViewById(R.id.text_image_name);
            textImageDescription = itemView.findViewById(R.id.text_image_description);
            textImageUrl = itemView.findViewById(R.id.text_image_url);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(ReviewImage image, OnImageClickListener listener) {
            textImageName.setText(image.getImageName());
            textImageDescription.setText(image.getDescription());
            textImageUrl.setText(image.getImageUrl());

            // Load image using ImageLoader with cache busting for fresh load
            ImageLoader.loadImageWithCacheBusting(
                itemView.getContext(), 
                image.getImageUrl(), 
                imageView, 
                R.drawable.ic_location_city
            );

            // Set click listeners
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(image);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(image);
                }
            });
        }
    }
}
