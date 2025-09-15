package com.ahmmedalmzini783.wcguide.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Landmark;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LandmarkAdapter extends RecyclerView.Adapter<LandmarkAdapter.LandmarkViewHolder> {

    private List<Landmark> landmarks;
    private OnLandmarkClickListener listener;

    public interface OnLandmarkClickListener {
        void onEditClick(Landmark landmark);
        void onDeleteClick(Landmark landmark);
        void onViewClick(Landmark landmark);
    }

    public LandmarkAdapter(List<Landmark> landmarks, OnLandmarkClickListener listener) {
        this.landmarks = landmarks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LandmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_landmark_admin, parent, false);
        return new LandmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LandmarkViewHolder holder, int position) {
        Landmark landmark = landmarks.get(position);
        holder.bind(landmark, listener);
    }

    @Override
    public int getItemCount() {
        return landmarks.size();
    }

    public void updateLandmarks(List<Landmark> newLandmarks) {
        this.landmarks.clear();
        this.landmarks.addAll(newLandmarks);
        notifyDataSetChanged();
    }

    static class LandmarkViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageLandmark;
        private TextView textLandmarkName;
        private TextView textLandmarkAddress;
        private TextView textLandmarkDescription;
        private MaterialButton btnView;
        private MaterialButton btnEdit;
        private MaterialButton btnDelete;

        public LandmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            imageLandmark = itemView.findViewById(R.id.image_landmark);
            textLandmarkName = itemView.findViewById(R.id.text_landmark_name);
            textLandmarkAddress = itemView.findViewById(R.id.text_landmark_address);
            textLandmarkDescription = itemView.findViewById(R.id.text_landmark_description);
            btnView = itemView.findViewById(R.id.btn_view);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Landmark landmark, OnLandmarkClickListener listener) {
            textLandmarkName.setText(landmark.getName());
            textLandmarkAddress.setText(landmark.getAddress());
            textLandmarkDescription.setText(landmark.getDescription());

            // Load image using Glide
            if (landmark.getImageUrl() != null && !landmark.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(landmark.getImageUrl())
                        .placeholder(R.drawable.ic_location_city)
                        .error(R.drawable.ic_location_city)
                        .centerCrop()
                        .into(imageLandmark);
            } else {
                imageLandmark.setImageResource(R.drawable.ic_location_city);
            }

            // Set click listeners
            btnView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewClick(landmark);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(landmark);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(landmark);
                }
            });
        }
    }
}
