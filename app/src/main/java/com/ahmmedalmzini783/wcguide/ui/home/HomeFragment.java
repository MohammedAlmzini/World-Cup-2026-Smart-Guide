package com.ahmmedalmzini783.wcguide.ui.home;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.databinding.FragmentHomeBinding;
import com.ahmmedalmzini783.wcguide.util.DateTimeUtil;
import com.ahmmedalmzini783.wcguide.ui.hotels.AllHotelsActivity;
import com.ahmmedalmzini783.wcguide.ui.googleplaces.GooglePlacesAdapter;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    // Adapters for different sections
    private BannerAdapter bannerAdapter;
    private PlaceAdapter attractionsAdapter;
    private GooglePlacesAdapter hotelsAdapter;
    private GooglePlacesAdapter restaurantsAdapter;
    
    // Google Places section
    private GooglePlacesAdapter placesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupRecyclerViews();
        setupPlacesSection();
        setupHotelsViewMoreButton();
        setupRestaurantsViewMoreButton();
        observeViewModel();

        // Start countdown timer
        updateCountdown();
    }

    private void setupRecyclerViews() {
        // Setup Banners RecyclerView
        if (binding.bannersRecycler != null) {
            bannerAdapter = new BannerAdapter(banner -> {
                // Open banner detail activity when clicked
                startActivity(com.ahmmedalmzini783.wcguide.ui.banner.BannerDetailActivity.createIntent(getContext(), banner));
            });
            
            LinearLayoutManager bannersLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            binding.bannersRecycler.setLayoutManager(bannersLayoutManager);
            binding.bannersRecycler.setAdapter(bannerAdapter);
            
            // Add performance optimizations
            binding.bannersRecycler.setHasFixedSize(true);
            binding.bannersRecycler.setItemViewCacheSize(20);
            binding.bannersRecycler.setNestedScrollingEnabled(false);
        }



        // Setup Hotels RecyclerView
        if (binding.hotelsRecycler != null) {
            hotelsAdapter = new GooglePlacesAdapter(place -> {
                // Handle hotel click - navigate to Google Place details
                Intent intent = new Intent(getContext(), com.ahmmedalmzini783.wcguide.ui.googleplaces.GooglePlaceDetailsActivity.class);
                intent.putExtra("place_id", place.getPlaceId());
                intent.putExtra("place_name", place.getName());
                startActivity(intent);
            });
            
            GridLayoutManager hotelsLayoutManager = new GridLayoutManager(getContext(), 2);
            binding.hotelsRecycler.setLayoutManager(hotelsLayoutManager);
            binding.hotelsRecycler.setAdapter(hotelsAdapter);
            binding.hotelsRecycler.setHasFixedSize(true);
            binding.hotelsRecycler.setNestedScrollingEnabled(false);
        }

        // Setup Restaurants RecyclerView
        if (binding.restaurantsRecycler != null) {
            restaurantsAdapter = new GooglePlacesAdapter(place -> {
                // Handle restaurant click - navigate to Google Place details
                Intent intent = new Intent(getContext(), com.ahmmedalmzini783.wcguide.ui.googleplaces.GooglePlaceDetailsActivity.class);
                intent.putExtra("place_id", place.getPlaceId());
                intent.putExtra("place_name", place.getName());
                startActivity(intent);
            });
            
            GridLayoutManager restaurantsLayoutManager = new GridLayoutManager(getContext(), 2);
            binding.restaurantsRecycler.setLayoutManager(restaurantsLayoutManager);
            binding.restaurantsRecycler.setAdapter(restaurantsAdapter);
            binding.restaurantsRecycler.setHasFixedSize(true);
            binding.restaurantsRecycler.setNestedScrollingEnabled(false);
        }
    }

    private void observeViewModel() {
        // Observe banners data
        if (viewModel != null && viewModel.getBanners() != null) {
            viewModel.getBanners().observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.getData() != null && bannerAdapter != null) {
                    bannerAdapter.submitList(resource.getData());
                }
            });
        }
        
        // Observe attractions data
        if (viewModel != null && viewModel.getAttractions() != null) {
            viewModel.getAttractions().observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.getData() != null && attractionsAdapter != null) {
                    attractionsAdapter.submitList(resource.getData());
                }
            });
        }
        
        // Observe hotels data
        if (viewModel != null && viewModel.getHotels() != null) {
            Log.d(TAG, "Setting up hotels observer");
            viewModel.getHotels().observe(getViewLifecycleOwner(), resource -> {
                Log.d(TAG, "Hotels resource received with status: " + (resource != null ? resource.getStatus() : "null"));
                
                if (resource != null) {
                    switch (resource.getStatus()) {
                        case LOADING:
                            Log.d(TAG, "Hotels loading...");
                            if (binding.hotelsProgressBar != null) {
                                binding.hotelsProgressBar.setVisibility(View.VISIBLE);
                            }
                            if (binding.hotelsEmptyState != null) {
                                binding.hotelsEmptyState.setVisibility(View.GONE);
                            }
                            break;
                        case SUCCESS:
                            if (binding.hotelsProgressBar != null) {
                                binding.hotelsProgressBar.setVisibility(View.GONE);
                            }
                            
                            if (resource.getData() != null && !resource.getData().isEmpty() && hotelsAdapter != null) {
                                Log.d(TAG, "Submitting " + resource.getData().size() + " hotels to adapter");
                                hotelsAdapter.submitList(resource.getData());
                                if (binding.hotelsEmptyState != null) {
                                    binding.hotelsEmptyState.setVisibility(View.GONE);
                                }
                            } else {
                                Log.d(TAG, "No hotels data available");
                                if (binding.hotelsEmptyState != null) {
                                    binding.hotelsEmptyState.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                        case ERROR:
                            Log.e(TAG, "Error loading hotels: " + resource.getMessage());
                            if (binding.hotelsProgressBar != null) {
                                binding.hotelsProgressBar.setVisibility(View.GONE);
                            }
                            if (binding.hotelsEmptyState != null) {
                                binding.hotelsEmptyState.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                } else {
                    Log.d(TAG, "Hotels resource is null");
                }
            });
        } else {
            Log.e(TAG, "Hotels LiveData is null in ViewModel");
        }
        
        // Observe restaurants data
        if (viewModel != null && viewModel.getRestaurants() != null) {
            Log.d(TAG, "Setting up restaurants observer");
            viewModel.getRestaurants().observe(getViewLifecycleOwner(), resource -> {
                Log.d(TAG, "Restaurants resource received with status: " + (resource != null ? resource.getStatus() : "null"));
                
                if (resource != null) {
                    switch (resource.getStatus()) {
                        case LOADING:
                            Log.d(TAG, "Restaurants loading...");
                            if (binding.restaurantsProgressBar != null) {
                                binding.restaurantsProgressBar.setVisibility(View.VISIBLE);
                            }
                            if (binding.restaurantsEmptyState != null) {
                                binding.restaurantsEmptyState.setVisibility(View.GONE);
                            }
                            break;
                        case SUCCESS:
                            if (binding.restaurantsProgressBar != null) {
                                binding.restaurantsProgressBar.setVisibility(View.GONE);
                            }
                            
                            if (resource.getData() != null && !resource.getData().isEmpty() && restaurantsAdapter != null) {
                                Log.d(TAG, "Submitting " + resource.getData().size() + " restaurants to adapter");
                                restaurantsAdapter.submitList(resource.getData());
                                if (binding.restaurantsEmptyState != null) {
                                    binding.restaurantsEmptyState.setVisibility(View.GONE);
                                }
                            } else {
                                Log.d(TAG, "No restaurants data available");
                                if (binding.restaurantsEmptyState != null) {
                                    binding.restaurantsEmptyState.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                        case ERROR:
                            Log.e(TAG, "Error loading restaurants: " + resource.getMessage());
                            if (binding.restaurantsProgressBar != null) {
                                binding.restaurantsProgressBar.setVisibility(View.GONE);
                            }
                            if (binding.restaurantsEmptyState != null) {
                                binding.restaurantsEmptyState.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                } else {
                    Log.d(TAG, "Restaurants resource is null");
                }
            });
        } else {
            Log.e(TAG, "Restaurants LiveData is null in ViewModel");
        }
    }

    private void updateCountdown() {
        if (binding == null) return;

        if (getContext() != null) {
            long now = System.currentTimeMillis();
            long diff = DateTimeUtil.WORLD_CUP_START_TIME - now;

            if (diff <= 0) {
                // Show started message; hide segmented container
                binding.countdownSegmentContainer.setVisibility(View.GONE);
                binding.countdownText.setVisibility(View.VISIBLE);
                binding.countdownText.setText(DateTimeUtil.getWorldCupCountdown(getContext()));
                
                // Add celebration animation
                animateFinishedState();
            } else {
                // Show segmented values and hide status text
                binding.countdownSegmentContainer.setVisibility(View.VISIBLE);
                binding.countdownText.setVisibility(View.GONE);

                long days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diff);
                long hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(diff) % 24;
                long minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
                long seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(diff) % 60;

                // Update values with animation for seconds
                updateValueWithAnimation(binding.countdownDaysValue, String.valueOf(days));
                updateValueWithAnimation(binding.countdownHoursValue, String.format(Locale.ENGLISH, "%02d", hours));
                updateValueWithAnimation(binding.countdownMinutesValue, String.format(Locale.ENGLISH, "%02d", minutes));
                updateValueWithAnimation(binding.countdownSecondsValue, String.format(Locale.ENGLISH, "%02d", seconds));
                
                // Add pulse animation to seconds
                if (seconds != getLastSecondsValue()) {
                    pulseAnimation(binding.countdownSecondsValue);
                    setLastSecondsValue(seconds);
                }
            }
        }

        // Update every second
        if (binding != null) {
            binding.getRoot().postDelayed(this::updateCountdown, 1000);
        }
    }

    private long lastSecondsValue = -1;
    
    private long getLastSecondsValue() {
        return lastSecondsValue;
    }
    
    private void setLastSecondsValue(long value) {
        this.lastSecondsValue = value;
    }
    
    private void updateValueWithAnimation(android.widget.TextView textView, String newValue) {
        if (!textView.getText().toString().equals(newValue)) {
            // Scale animation when value changes
            textView.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(150)
                .withEndAction(() -> {
                    textView.setText(newValue);
                    textView.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(150)
                        .start();
                })
                .start();
        } else {
            textView.setText(newValue);
        }
    }
    
    private void pulseAnimation(View view) {
        ValueAnimator pulseAnimator = ValueAnimator.ofFloat(1.0f, 1.15f, 1.0f);
        pulseAnimator.setDuration(600);
        pulseAnimator.setInterpolator(new DecelerateInterpolator());
        pulseAnimator.addUpdateListener(animation -> {
            float scale = (Float) animation.getAnimatedValue();
            view.setScaleX(scale);
            view.setScaleY(scale);
        });
        pulseAnimator.start();
    }
    
    private void animateFinishedState() {
        // Celebration animation when countdown finishes
        binding.countdownText.setScaleX(0.5f);
        binding.countdownText.setScaleY(0.5f);
        binding.countdownText.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(500)
            .withEndAction(() -> {
                binding.countdownText.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(300)
                    .start();
            })
            .start();
    }
    
    /**
     * Force refresh data from Firebase, bypassing cache
     */
    public void forceRefreshData() {
        if (viewModel != null) {
            // Clear Glide cache to ensure fresh images
            if (getContext() != null) {
                com.bumptech.glide.Glide.get(getContext()).clearMemory();
                // Clear disk cache in background thread
                new Thread(() -> {
                    com.bumptech.glide.Glide.get(getContext()).clearDiskCache();
                }).start();
            }
            
            // Refresh ViewModel data
            viewModel.refreshData();
        }
    }

    private void setupPlacesSection() {
        // Setup RecyclerView for Google Places
        placesAdapter = new GooglePlacesAdapter(place -> {
            // Handle place click
            Intent intent = new Intent(getContext(), com.ahmmedalmzini783.wcguide.ui.googleplaces.GooglePlaceDetailsActivity.class);
            intent.putExtra("place", place);
            startActivity(intent);
        });
        binding.recyclerPlaces.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerPlaces.setAdapter(placesAdapter);
        
        // Setup View More button
        binding.btnViewAllPlaces.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.ahmmedalmzini783.wcguide.ui.googleplaces.GooglePlacesActivity.class);
                startActivity(intent);
        });

        // Load Google Places data
        loadGooglePlaces();
    }

    private void loadGooglePlaces() {
        // Load Google Places data using the existing GooglePlacesViewModel
        com.ahmmedalmzini783.wcguide.ui.googleplaces.GooglePlacesViewModel placesViewModel = 
                new ViewModelProvider(this).get(com.ahmmedalmzini783.wcguide.ui.googleplaces.GooglePlacesViewModel.class);
        
        // Load tourist attractions
        placesViewModel.getPlaces("attraction").observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.getData() != null && !resource.getData().isEmpty()) {
                placesAdapter.submitList(resource.getData());
            }
        });
        
        // Load places for all countries
        placesViewModel.loadPlaces("attraction");
    }


    private void setupHotelsViewMoreButton() {
        if (binding.buttonViewMoreHotels != null) {
            binding.buttonViewMoreHotels.setOnClickListener(v -> {
                Log.d(TAG, "View more hotels button clicked");
                Intent intent = AllHotelsActivity.createIntent(getContext());
                startActivity(intent);
            });
        }
    }

    private void setupRestaurantsViewMoreButton() {
        if (binding.buttonViewMoreRestaurants != null) {
            binding.buttonViewMoreRestaurants.setOnClickListener(v -> {
                Log.d(TAG, "View more restaurants button clicked");
                Intent intent = com.ahmmedalmzini783.wcguide.ui.restaurants.AllRestaurantsActivity.createIntent(getContext());
                startActivity(intent);
            });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}