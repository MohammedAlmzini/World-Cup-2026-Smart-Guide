package com.ahmmedalmzini783.wcguide.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.databinding.ItemPlaceHorizontalBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class PlaceAdapter extends ListAdapter<Place, PlaceAdapter.PlaceViewHolder> {

    private final OnPlaceClickListener listener;

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
    }

    public PlaceAdapter(OnPlaceClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Place> DIFF_CALLBACK = new DiffUtil.ItemCallback<Place>() {
        @Override
        public boolean areItemsTheSame(@NonNull Place oldItem, @NonNull Place newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Place oldItem, @NonNull Place newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaceHorizontalBinding binding = ItemPlaceHorizontalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PlaceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlaceHorizontalBinding binding;

        PlaceViewHolder(ItemPlaceHorizontalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Place place) {
            binding.placeName.setText(place.getName());
            binding.placeAddress.setText(place.getCity() + ", " + place.getCountry());
            binding.placeRating.setText(String.valueOf(place.getAvgRating()));
            
            // Set price level
            StringBuilder priceText = new StringBuilder();
            for (int i = 0; i < place.getPriceLevel(); i++) {
                priceText.append("$");
            }
            binding.placePrice.setText(priceText.toString());

            // Load image if available
            if (place.getImages() != null && !place.getImages().isEmpty()) {
                Glide.with(binding.placeImage.getContext())
                        .load(place.getImages().get(0))
                        .centerCrop()
                        .into(binding.placeImage);
            }

            // Set click listener
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlaceClick(place);
                }
            });

            // Set favorite button click listener
            binding.favoriteButton.setOnClickListener(v -> {
                // TODO: Implement favorite functionality
                // For now, just toggle the icon
                if (binding.favoriteButton.getDrawable().getConstantState() == 
                    binding.favoriteButton.getContext().getDrawable(android.R.drawable.btn_star_big_off).getConstantState()) {
                    binding.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                } else {
                    binding.favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                }
            });
        }
    }
}