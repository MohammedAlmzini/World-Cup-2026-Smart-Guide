package com.ahmmedalmzini783.wcguide.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Restaurant;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurants = new ArrayList<>();
    private final OnRestaurantClickListener onRestaurantClickListener;
    private final OnRestaurantDeleteListener onRestaurantDeleteListener;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    public interface OnRestaurantDeleteListener {
        void onRestaurantDelete(Restaurant restaurant);
    }

    public RestaurantAdapter(OnRestaurantClickListener onRestaurantClickListener, 
                           OnRestaurantDeleteListener onRestaurantDeleteListener) {
        this.onRestaurantClickListener = onRestaurantClickListener;
        this.onRestaurantDeleteListener = onRestaurantDeleteListener;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants != null ? restaurants : new ArrayList<>();
        notifyDataSetChanged();
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView restaurantImage;
        private final TextView restaurantName;
        private final TextView restaurantLocation;
        private final TextView restaurantRating;
        private final TextView restaurantStatus;
        private final TextView restaurantCuisine;
        private final TextView restaurantPriceRange;
        private final MaterialButton btnEdit;
        private final MaterialButton btnDelete;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_restaurant);
            restaurantImage = itemView.findViewById(R.id.iv_restaurant_image);
            restaurantName = itemView.findViewById(R.id.tv_restaurant_name);
            restaurantLocation = itemView.findViewById(R.id.tv_restaurant_location);
            restaurantRating = itemView.findViewById(R.id.tv_restaurant_rating);
            restaurantStatus = itemView.findViewById(R.id.tv_restaurant_status);
            restaurantCuisine = itemView.findViewById(R.id.tv_restaurant_cuisine);
            restaurantPriceRange = itemView.findViewById(R.id.tv_restaurant_price_range);
            btnEdit = itemView.findViewById(R.id.btn_edit_restaurant);
            btnDelete = itemView.findViewById(R.id.btn_delete_restaurant);
        }

        public void bind(Restaurant restaurant) {
            restaurantName.setText(restaurant.getName());
            restaurantLocation.setText(restaurant.getCity() + ", " + restaurant.getCountry());
            restaurantRating.setText(String.format("%.1f", restaurant.getRating()));
            
            // Set status
            if (restaurant.isOpen()) {
                restaurantStatus.setText("مفتوح");
                restaurantStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
            } else {
                restaurantStatus.setText("مغلق");
                restaurantStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
            }

            // Set cuisine type
            restaurantCuisine.setText(restaurant.getCuisineType() != null ? restaurant.getCuisineType() : "غير محدد");

            // Set price range
            restaurantPriceRange.setText(restaurant.getPriceRange() != null ? restaurant.getPriceRange() : "$");

            // Load restaurant image
            if (restaurant.getMainImageUrl() != null && !restaurant.getMainImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(restaurant.getMainImageUrl())
                        .placeholder(R.drawable.ic_restaurant_placeholder)
                        .error(R.drawable.ic_restaurant_placeholder)
                        .into(restaurantImage);
            } else {
                restaurantImage.setImageResource(R.drawable.ic_restaurant_placeholder);
            }

            // Set click listeners
            cardView.setOnClickListener(v -> {
                if (onRestaurantClickListener != null) {
                    onRestaurantClickListener.onRestaurantClick(restaurant);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (onRestaurantClickListener != null) {
                    onRestaurantClickListener.onRestaurantClick(restaurant);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (onRestaurantDeleteListener != null) {
                    onRestaurantDeleteListener.onRestaurantDelete(restaurant);
                }
            });
        }
    }
}
