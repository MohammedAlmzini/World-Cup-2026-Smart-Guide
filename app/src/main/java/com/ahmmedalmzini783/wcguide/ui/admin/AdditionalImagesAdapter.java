package com.ahmmedalmzini783.wcguide.ui.admin;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Hotel;

import java.util.ArrayList;
import java.util.List;

public class AdditionalImagesAdapter extends RecyclerView.Adapter<AdditionalImagesAdapter.ImageViewHolder> {

    private List<Hotel.AdditionalImage> images = new ArrayList<>();

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_additional_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Hotel.AdditionalImage image = images.get(position);
        holder.bind(image, position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setImages(List<Hotel.AdditionalImage> images) {
        this.images = images != null ? new ArrayList<>(images) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addImage(Hotel.AdditionalImage image) {
        images.add(image);
        notifyItemInserted(images.size() - 1);
    }

    public List<Hotel.AdditionalImage> getImages() {
        return new ArrayList<>(images);
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private final EditText etImageUrl;
        private final EditText etImageName;
        private final ImageButton btnRemove;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            etImageUrl = itemView.findViewById(R.id.et_image_url);
            etImageName = itemView.findViewById(R.id.et_image_name);
            btnRemove = itemView.findViewById(R.id.btn_remove_image);
        }

        public void bind(Hotel.AdditionalImage image, int position) {
            // Remove previous text watchers to avoid conflicts
            etImageUrl.setTag(null);
            etImageName.setTag(null);

            // Set initial values
            etImageUrl.setText(image.getImageUrl());
            etImageName.setText(image.getImageName());

            // Add text watchers
            etImageUrl.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    image.setImageUrl(s.toString());
                }
            });

            etImageName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    image.setImageName(s.toString());
                }
            });

            // Remove button click listener
            btnRemove.setOnClickListener(v -> {
                int currentPosition = getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    images.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, images.size());
                }
            });
        }
    }
}
