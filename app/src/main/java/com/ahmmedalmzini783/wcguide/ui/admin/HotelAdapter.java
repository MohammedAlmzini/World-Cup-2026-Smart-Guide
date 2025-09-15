package com.ahmmedalmzini783.wcguide.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Hotel;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private List<Hotel> hotels = new ArrayList<>();
    private final OnHotelClickListener onHotelClickListener;
    private final OnHotelDeleteListener onHotelDeleteListener;

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel);
    }

    public interface OnHotelDeleteListener {
        void onHotelDelete(Hotel hotel);
    }

    public HotelAdapter(OnHotelClickListener onHotelClickListener, 
                       OnHotelDeleteListener onHotelDeleteListener) {
        this.onHotelClickListener = onHotelClickListener;
        this.onHotelDeleteListener = onHotelDeleteListener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotels.get(position);
        holder.bind(hotel);
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    public void setHotels(List<Hotel> hotels) {
        this.hotels = hotels != null ? hotels : new ArrayList<>();
        notifyDataSetChanged();
    }

    class HotelViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView hotelImage;
        private final TextView hotelName;
        private final TextView hotelLocation;
        private final TextView hotelRating;
        private final TextView hotelStatus;
        private final TextView hotelRoomsCount;
        private final MaterialButton btnEdit;
        private final MaterialButton btnDelete;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_hotel);
            hotelImage = itemView.findViewById(R.id.iv_hotel_image);
            hotelName = itemView.findViewById(R.id.tv_hotel_name);
            hotelLocation = itemView.findViewById(R.id.tv_hotel_location);
            hotelRating = itemView.findViewById(R.id.tv_hotel_rating);
            hotelStatus = itemView.findViewById(R.id.tv_hotel_status);
            hotelRoomsCount = itemView.findViewById(R.id.tv_hotel_rooms_count);
            btnEdit = itemView.findViewById(R.id.btn_edit_hotel);
            btnDelete = itemView.findViewById(R.id.btn_delete_hotel);
        }

        public void bind(Hotel hotel) {
            hotelName.setText(hotel.getName());
            hotelLocation.setText(hotel.getCity() + ", " + hotel.getCountry());
            hotelRating.setText(String.format("%.1f", hotel.getRating()));
            
            // Set status
            if (hotel.isOpen()) {
                hotelStatus.setText("مفتوح");
                hotelStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
            } else {
                hotelStatus.setText("مغلق");
                hotelStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
            }

            // Set rooms count
            int roomsCount = hotel.getRooms() != null ? hotel.getRooms().size() : 0;
            hotelRoomsCount.setText(roomsCount + " غرفة");

            // Load hotel image
            if (hotel.getMainImageUrl() != null && !hotel.getMainImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(hotel.getMainImageUrl())
                        .placeholder(R.drawable.ic_hotel_placeholder)
                        .error(R.drawable.ic_hotel_placeholder)
                        .into(hotelImage);
            } else {
                hotelImage.setImageResource(R.drawable.ic_hotel_placeholder);
            }

            // Set click listeners
            cardView.setOnClickListener(v -> {
                if (onHotelClickListener != null) {
                    onHotelClickListener.onHotelClick(hotel);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (onHotelClickListener != null) {
                    onHotelClickListener.onHotelClick(hotel);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (onHotelDeleteListener != null) {
                    onHotelDeleteListener.onHotelDelete(hotel);
                }
            });
        }
    }
}
