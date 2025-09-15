package com.example.wcguide.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Landmark;
import com.ahmmedalmzini783.wcguide.util.ImageLoader;

public class LandmarkHomeAdapter extends ListAdapter<Landmark, LandmarkHomeAdapter.LandmarkViewHolder> {

    public interface OnLandmarkClickListener {
        void onLandmarkClick(Landmark landmark);
    }

    private final OnLandmarkClickListener clickListener;

    public LandmarkHomeAdapter(OnLandmarkClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<Landmark> DIFF_CALLBACK = 
        new DiffUtil.ItemCallback<Landmark>() {
            @Override
            public boolean areItemsTheSame(@NonNull Landmark oldItem, @NonNull Landmark newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Landmark oldItem, @NonNull Landmark newItem) {
                return oldItem.equals(newItem);
            }
        };

    @NonNull
    @Override
    public LandmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_landmark_home, parent, false);
        return new LandmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LandmarkViewHolder holder, int position) {
        Landmark landmark = getItem(position);
        holder.bind(landmark);
    }

    class LandmarkViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageLandmark;
        private final TextView textLandmarkName;
        private final TextView textLandmarkDescription;
        private final RatingBar ratingBar;
        private final TextView textRating;
        private final TextView textLocation;
        private final ProgressBar progressBar;

        public LandmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            imageLandmark = itemView.findViewById(R.id.image_landmark);
            textLandmarkName = itemView.findViewById(R.id.text_landmark_name);
            textLandmarkDescription = itemView.findViewById(R.id.text_landmark_description);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            textRating = itemView.findViewById(R.id.text_rating);
            textLocation = itemView.findViewById(R.id.text_location);
            progressBar = itemView.findViewById(R.id.progress_bar);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onLandmarkClick(getItem(position));
                }
            });
        }

        public void bind(Landmark landmark) {
            Context context = itemView.getContext();
            
            // Set landmark name
            textLandmarkName.setText(landmark.getName());
            
            // Set description with truncation
            String description = landmark.getDescription();
            if (description != null && description.length() > 60) {
                description = description.substring(0, 60) + "...";
            }
            textLandmarkDescription.setText(description);
            
            // Set location
            textLocation.setText(landmark.getAddress());
            
            // Set rating
            float rating = landmark.getRating();
            ratingBar.setRating(rating);
            textRating.setText(String.format("%.1f", rating));
            
            // Load image
            loadLandmarkImage(context, landmark);
        }

        private void loadLandmarkImage(Context context, Landmark landmark) {
            progressBar.setVisibility(View.VISIBLE);
            
            String imageUrl = landmark.getImageUrl();
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                ImageLoader.loadImageWithCacheBusting(
                    context,
                    imageUrl,
                    imageLandmark,
                    android.R.drawable.ic_menu_gallery
                );
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                imageLandmark.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }
    }
}
