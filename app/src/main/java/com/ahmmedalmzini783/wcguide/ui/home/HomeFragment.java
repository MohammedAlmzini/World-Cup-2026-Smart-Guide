package com.ahmmedalmzini783.wcguide.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.ahmmedalmzini783.wcguide.databinding.FragmentHomeBinding;
import com.ahmmedalmzini783.wcguide.util.DateTimeUtil;
import com.ahmmedalmzini783.wcguide.util.Resource;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    // Adapters
    private BannerAdapter bannerAdapter;
    private PlaceAdapter attractionsAdapter;
    private PlaceAdapter hotelsAdapter;
    private PlaceAdapter restaurantsAdapter;
    private QuickInfoAdapter quickInfoAdapter;

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
        setupClickListeners();
        observeViewModel();

        // Start countdown timer
        updateCountdown();
    }

    private void setupRecyclerViews() {
        // Banners RecyclerView with snap helper for auto-scroll effect
        bannerAdapter = new BannerAdapter(banner -> {
            // Handle banner click - navigate to deep link
        });
        binding.bannersRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.bannersRecycler.setAdapter(bannerAdapter);
        new PagerSnapHelper().attachToRecyclerView(binding.bannersRecycler);

        // Attractions RecyclerView
        attractionsAdapter = new PlaceAdapter(place -> {
            // Navigate to place details
        });
        binding.attractionsRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.attractionsRecycler.setAdapter(attractionsAdapter);

        // Hotels RecyclerView
        hotelsAdapter = new PlaceAdapter(place -> {
            // Navigate to place details
        });
        binding.hotelsRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.hotelsRecycler.setAdapter(hotelsAdapter);

        // Restaurants RecyclerView
        restaurantsAdapter = new PlaceAdapter(place -> {
            // Navigate to place details
        });
        binding.restaurantsRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.restaurantsRecycler.setAdapter(restaurantsAdapter);

        // Quick Info RecyclerView
        quickInfoAdapter = new QuickInfoAdapter();
        binding.quickInfoRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.quickInfoRecycler.setAdapter(quickInfoAdapter);
    }

    private void setupClickListeners() {
        binding.viewMoreAttractions.setOnClickListener(v -> {
            // Navigate to places screen with attraction filter
        });

        binding.viewMoreHotels.setOnClickListener(v -> {
            // Navigate to places screen with hotel filter
        });

        binding.viewMoreRestaurants.setOnClickListener(v -> {
            // Navigate to places screen with restaurant filter
        });
    }

    private void observeViewModel() {
        viewModel.getBanners().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        if (resource.getData() != null) {
                            bannerAdapter.submitList(resource.getData());
                        }
                        break;
                    case ERROR:
                        // Handle error
                        break;
                    case LOADING:
                        // Show loading state
                        break;
                }
            }
        });

        viewModel.getAttractions().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS) {
                attractionsAdapter.submitList(resource.getData());
            }
        });

        viewModel.getHotels().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS) {
                hotelsAdapter.submitList(resource.getData());
            }
        });

        viewModel.getRestaurants().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS) {
                restaurantsAdapter.submitList(resource.getData());
            }
        });

        viewModel.getQuickInfo().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS) {
                quickInfoAdapter.submitList(resource.getData());
            }
        });
    }

    private void updateCountdown() {
        if (getContext() != null) {
            String countdown = DateTimeUtil.getWorldCupCountdown(getContext());
            binding.countdownText.setText(countdown);
        }

        // Update every minute
        if (binding != null) {
            binding.getRoot().postDelayed(this::updateCountdown, 60000);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}