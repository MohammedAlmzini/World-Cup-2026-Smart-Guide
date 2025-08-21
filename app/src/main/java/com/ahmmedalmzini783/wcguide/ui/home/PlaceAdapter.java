package com.ahmmedalmzini783.wcguide.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.databinding.ItemPlaceHorizontalBinding;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class PlaceAdapter extends ListAdapter<Place, PlaceAdapter.PlaceViewHolder> {

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
    }

    private final OnPlaceClickListener listener;

    public PlaceAdapter(OnPlaceClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Place> DIFF_CALLBACK = new DiffUtil.ItemCallback<Place>() {
        @Override
        public boolean areItemsTheSame(@NonNull Place oldItem, @NonNull Place newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Place oldItem, @NonNull Place newItem) {
            // Rely on equals/hashCode by id to keep it simple for now
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

            String addressText = buildAddressText(place);
            binding.placeAddress.setText(addressText);

            String ratingText = String.format(Locale.getDefault(), "%.1f", place.getAvgRating());
            binding.placeRating.setText(ratingText);

            binding.placePrice.setText(priceLevelToString(place.getPriceLevel()));

            List<String> images = place.getImages();
            String firstImage = (images != null && !images.isEmpty()) ? images.get(0) : null;

            Glide.with(binding.placeImage.getContext())
                    .load(firstImage)
                    .placeholder(R.drawable.ic_location)
                    .centerCrop()
                    .into(binding.placeImage);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlaceClick(place);
                }
            });
        }

        private String buildAddressText(Place place) {
            String city = place.getCity();
            String country = place.getCountry();
            if (city != null && !city.isEmpty() && country != null && !country.isEmpty()) {
                return city + ", " + country;
            }
            String address = place.getAddress();
            if (address != null && !address.isEmpty()) {
                return address;
            }
            if (city != null && !city.isEmpty()) {
                return city;
            }
            return country != null ? country : "";
        }

        private String priceLevelToString(int priceLevel) {
            if (priceLevel <= 0) return "";
            int level = Math.min(priceLevel, 4);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < level; i++) {
                builder.append('$');
            }
            return builder.toString();
        }
    }
}

