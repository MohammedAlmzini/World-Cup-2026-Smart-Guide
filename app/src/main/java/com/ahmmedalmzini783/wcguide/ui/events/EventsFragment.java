package com.ahmmedalmzini783.wcguide.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmmedalmzini783.wcguide.databinding.FragmentEventsBinding;
import com.ahmmedalmzini783.wcguide.util.Resource;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;
    private EventsViewModel viewModel;
    private EventAdapter eventAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EventsViewModel.class);

        setupRecyclerView();
        setupFilters();
        setupSwipeRefresh();
        observeViewModel();

        // Load initial data
        viewModel.loadEvents();
    }

    private void setupRecyclerView() {
        eventAdapter = new EventAdapter(event -> {
            // Navigate to event details
            // NavController.navigate(EventDetailsFragment.newInstance(event.getId()))
        }, (event, isFavorite) -> {
            // Handle favorite toggle
            viewModel.toggleFavorite(event.getId(), "event", isFavorite);
        });

        binding.eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.eventsRecycler.setAdapter(eventAdapter);
    }

    private void setupFilters() {
        // Country filter
        binding.chipCountry.setOnClickListener(v -> {
            // Show country selection dialog
            showCountryFilterDialog();
        });

        // City filter
        binding.chipCity.setOnClickListener(v -> {
            // Show city selection dialog
            showCityFilterDialog();
        });

        // Type filter
        binding.chipType.setOnClickListener(v -> {
            // Show type selection dialog
            showTypeFilterDialog();
        });

        // Date filter
        binding.chipDate.setOnClickListener(v -> {
            // Show date picker dialog
            showDateFilterDialog();
        });

        // Clear filters
        binding.clearFilters.setOnClickListener(v -> {
            viewModel.clearFilters();
            updateFilterChips();
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.refreshEvents();
        });
    }

    private void observeViewModel() {
        viewModel.getEvents().observe(getViewLifecycleOwner(), resource -> {
            binding.swipeRefresh.setRefreshing(false);

            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        binding.emptyState.setVisibility(View.GONE);
                        binding.eventsRecycler.setVisibility(View.VISIBLE);

                        if (resource.getData() != null && !resource.getData().isEmpty()) {
                            eventAdapter.submitList(resource.getData());
                        } else {
                            showEmptyState();
                        }
                        break;

                    case ERROR:
                        binding.emptyState.setVisibility(View.GONE);
                        binding.eventsRecycler.setVisibility(View.VISIBLE);
                        // Show error message
                        break;

                    case LOADING:
                        if (eventAdapter.getItemCount() == 0) {
                            binding.swipeRefresh.setRefreshing(true);
                        }
                        break;
                }
            }
        });

        viewModel.getFilterState().observe(getViewLifecycleOwner(), filterState -> {
            updateFilterChips();
        });
    }

    private void showEmptyState() {
        binding.eventsRecycler.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.VISIBLE);
    }

    private void updateFilterChips() {
        EventsViewModel.FilterState filterState = viewModel.getFilterState().getValue();
        if (filterState != null) {
            // Update chip appearances based on active filters
            binding.chipCountry.setChecked(filterState.getSelectedCountry() != null);
            binding.chipCity.setChecked(filterState.getSelectedCity() != null);
            binding.chipType.setChecked(filterState.getSelectedType() != null);
            binding.chipDate.setChecked(filterState.getSelectedDateRange() != null);
        }
    }

    private void showCountryFilterDialog() {
        // TODO: Implement country filter dialog
    }

    private void showCityFilterDialog() {
        // TODO: Implement city filter dialog
    }

    private void showTypeFilterDialog() {
        // TODO: Implement type filter dialog
    }

    private void showDateFilterDialog() {
        // TODO: Implement date filter dialog
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}