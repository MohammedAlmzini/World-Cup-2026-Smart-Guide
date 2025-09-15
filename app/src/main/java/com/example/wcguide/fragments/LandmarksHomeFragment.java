package com.example.wcguide.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ahmmedalmzini783.wcguide.R;
import com.example.wcguide.activities.AllLandmarksActivity;
import com.example.wcguide.viewmodels.LandmarkHomeViewModel;
import com.ahmmedalmzini783.wcguide.data.model.Landmark;
import com.ahmmedalmzini783.wcguide.data.repository.LandmarkRepository;
import com.ahmmedalmzini783.wcguide.util.ImageLoader;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LandmarksHomeFragment extends Fragment {

    private View sectionLandmarks;
    private TextView headerTitle;
    private MaterialButton buttonViewMore;
    private View emptyStateView;
    private ProgressBar progressBar;
    
    // Landmark card views
    private View cardLandmark1, cardLandmark2, cardLandmark3, cardLandmark4;
    
    private LandmarkHomeViewModel landmarkViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.section_landmarks_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupViewModel();
        setupClickListeners();
        observeData();
    }

    private void initViews(View view) {
        sectionLandmarks = view.findViewById(R.id.section_landmarks);
        headerTitle = view.findViewById(R.id.text_header_title);
        buttonViewMore = view.findViewById(R.id.button_view_more);
        emptyStateView = view.findViewById(R.id.layout_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
        
        cardLandmark1 = view.findViewById(R.id.card_landmark_1);
        cardLandmark2 = view.findViewById(R.id.card_landmark_2);
        cardLandmark3 = view.findViewById(R.id.card_landmark_3);
        cardLandmark4 = view.findViewById(R.id.card_landmark_4);
    }

    private void setupViewModel() {
        LandmarkRepository repository = new LandmarkRepository();
        LandmarkHomeViewModel.Factory factory = new LandmarkHomeViewModel.Factory(repository);
        landmarkViewModel = new ViewModelProvider(this, factory).get(LandmarkHomeViewModel.class);
    }

    private void setupClickListeners() {
        buttonViewMore.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AllLandmarksActivity.class);
            startActivity(intent);
        });
    }

    private void observeData() {
        landmarkViewModel.getLatestLandmarks(4).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case LOADING:
                        showLoading(true);
                        break;
                    case SUCCESS:
                        showLoading(false);
                        if (resource.getData() != null && !resource.getData().isEmpty()) {
                            displayLandmarks(resource.getData());
                            showEmptyState(false);
                        } else {
                            showEmptyState(true);
                        }
                        break;
                    case ERROR:
                        showLoading(false);
                        showEmptyState(true);
                        break;
                }
            }
        });
    }

    private void displayLandmarks(List<Landmark> landmarks) {
        View[] cards = {cardLandmark1, cardLandmark2, cardLandmark3, cardLandmark4};
        
        for (int i = 0; i < cards.length; i++) {
            if (i < landmarks.size()) {
                populateLandmarkCard(cards[i], landmarks.get(i));
                cards[i].setVisibility(View.VISIBLE);
            } else {
                cards[i].setVisibility(View.GONE);
            }
        }
        
        sectionLandmarks.setVisibility(View.VISIBLE);
    }

    private void populateLandmarkCard(View cardView, Landmark landmark) {
        ImageView imageLandmark = cardView.findViewById(R.id.image_landmark);
        TextView textLandmarkName = cardView.findViewById(R.id.text_landmark_name);
        TextView textLandmarkDescription = cardView.findViewById(R.id.text_landmark_description);
        RatingBar ratingBar = cardView.findViewById(R.id.rating_bar);
        TextView textRating = cardView.findViewById(R.id.text_rating);
        TextView textLocation = cardView.findViewById(R.id.text_location);
        ProgressBar progressBar = cardView.findViewById(R.id.progress_bar);

        // Set landmark data
        textLandmarkName.setText(landmark.getName());
        
        String description = landmark.getDescription();
        if (description != null && description.length() > 60) {
            description = description.substring(0, 60) + "...";
        }
        textLandmarkDescription.setText(description);
        
        textLocation.setText(landmark.getAddress());
        
        float rating = landmark.getRating();
        ratingBar.setRating(rating);
        textRating.setText(String.format("%.1f", rating));

        // Load image
        String imageUrl = landmark.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            ImageLoader.loadImageWithCacheBusting(
                getContext(),
                imageUrl,
                imageLandmark,
                android.R.drawable.ic_menu_gallery
            );
            progressBar.setVisibility(View.GONE);
        } else {
            imageLandmark.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Set click listener
        cardView.setOnClickListener(v -> {
            // Navigate to landmark details
            Intent intent = new Intent(getContext(), com.ahmmedalmzini783.wcguide.ui.admin.LandmarkDetailsActivity.class);
            intent.putExtra("landmark", landmark);
            startActivity(intent);
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        sectionLandmarks.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        emptyStateView.setVisibility(show ? View.VISIBLE : View.GONE);
        sectionLandmarks.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
