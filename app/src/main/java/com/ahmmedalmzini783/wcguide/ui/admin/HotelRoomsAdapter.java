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
import java.util.Arrays;
import java.util.List;

public class HotelRoomsAdapter extends RecyclerView.Adapter<HotelRoomsAdapter.RoomViewHolder> {

    private List<Hotel.HotelRoom> rooms = new ArrayList<>();

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hotel_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Hotel.HotelRoom room = rooms.get(position);
        holder.bind(room, position);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void setRooms(List<Hotel.HotelRoom> rooms) {
        this.rooms = rooms != null ? new ArrayList<>(rooms) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addRoom(Hotel.HotelRoom room) {
        rooms.add(room);
        notifyItemInserted(rooms.size() - 1);
    }

    public List<Hotel.HotelRoom> getRooms() {
        return new ArrayList<>(rooms);
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {
        private final EditText etRoomName;
        private final EditText etCapacity;
        private final EditText etPricePerNight;
        private final EditText etRoomFacilities;
        private final EditText etBookingUrl;
        private final ImageButton btnRemove;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            etRoomName = itemView.findViewById(R.id.et_room_name);
            etCapacity = itemView.findViewById(R.id.et_capacity);
            etPricePerNight = itemView.findViewById(R.id.et_price_per_night);
            etRoomFacilities = itemView.findViewById(R.id.et_room_facilities);
            etBookingUrl = itemView.findViewById(R.id.et_booking_url);
            btnRemove = itemView.findViewById(R.id.btn_remove_room);
        }

        public void bind(Hotel.HotelRoom room, int position) {
            // Set initial values
            etRoomName.setText(room.getRoomName());
            etCapacity.setText(String.valueOf(room.getCapacity()));
            etPricePerNight.setText(String.valueOf(room.getPricePerNight()));
            etBookingUrl.setText(room.getBookingUrl());
            
            if (room.getRoomFacilities() != null) {
                etRoomFacilities.setText(String.join(", ", room.getRoomFacilities()));
            }

            // Add text watchers
            etRoomName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    room.setRoomName(s.toString());
                }
            });

            etCapacity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        room.setCapacity(Integer.parseInt(s.toString()));
                    } catch (NumberFormatException e) {
                        room.setCapacity(1);
                    }
                }
            });

            etPricePerNight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        room.setPricePerNight(Double.parseDouble(s.toString()));
                    } catch (NumberFormatException e) {
                        room.setPricePerNight(0.0);
                    }
                }
            });

            etRoomFacilities.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString().trim();
                    if (!text.isEmpty()) {
                        room.setRoomFacilities(Arrays.asList(text.split("\\s*,\\s*")));
                    } else {
                        room.setRoomFacilities(new ArrayList<>());
                    }
                }
            });

            etBookingUrl.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    room.setBookingUrl(s.toString());
                }
            });

            // Remove button click listener
            btnRemove.setOnClickListener(v -> {
                int currentPosition = getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    rooms.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, rooms.size());
                }
            });
        }
    }
}
