package com.ahmmedalmzini783.wcguide.ui.events;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.ahmmedalmzini783.wcguide.util.Resource;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    private RecyclerView eventsRecyclerView;
    private EventsViewModel viewModel;
    private EventAdapter eventAdapter;
    private View rootView;
    private AlertDialog filterDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    
    // Featured event views
    private View featuredEventLayout;
    private TextView featuredEventTitle;
    private TextView featuredEventDescription;
    private TextView featuredEventDate;
    private TextView featuredEventLocation;
    private ImageView featuredEventImage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_events, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(EventsViewModel.class);
        
        // Setup UI
        setupRecyclerView();
        setupFilters();
        setupSwipeRefresh();
        observeViewModel();
        
        // Load events only if not already loaded
        if (eventAdapter == null || eventAdapter.getItemCount() == 0) {
            viewModel.loadEvents();
        }
    }

    private void setupRecyclerView() {
        eventsRecyclerView = rootView.findViewById(R.id.events_recycler);
        if (eventsRecyclerView != null) {
            eventAdapter = new EventAdapter(new ArrayList<>(), this::onEventClick);
            eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            eventsRecyclerView.setAdapter(eventAdapter);
        }
        
        // Setup featured event views
        setupFeaturedEventViews();
    }
    
    private void setupFeaturedEventViews() {
        featuredEventLayout = rootView.findViewById(R.id.featured_event_section);
        featuredEventTitle = rootView.findViewById(R.id.featured_event_title);
        featuredEventDescription = rootView.findViewById(R.id.featured_event_description);
        featuredEventDate = rootView.findViewById(R.id.featured_event_date);
        featuredEventLocation = rootView.findViewById(R.id.featured_event_location);
        featuredEventImage = rootView.findViewById(R.id.featured_event_image);
        
        // Set click listener for featured event
        if (featuredEventLayout != null) {
            featuredEventLayout.setOnClickListener(v -> {
                // Handle featured event click - open event details
                Event featuredEvent = viewModel.getFeaturedEvent().getValue() != null ? 
                    viewModel.getFeaturedEvent().getValue().getData() : null;
                if (featuredEvent != null) {
                    onEventClick(featuredEvent);
                }
            });
        }
    }

    private void setupFilters() {
        // إعداد زر الفلتر
        View filterButton = rootView.findViewById(R.id.filter_button);
        if (filterButton != null) {
            filterButton.setOnClickListener(v -> showFilterDialog());
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                // إعادة تحميل الفعاليات عند السحب للتحديث
                viewModel.refreshEvents();
            });
            
            // إعداد ألوان التحديث
            swipeRefreshLayout.setColorSchemeResources(
                R.color.primary,
                R.color.primary_dark,
                R.color.secondary
            );
        }
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null);
        
        builder.setView(dialogView);
        filterDialog = builder.create();
        filterDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // إعداد الفلاتر
        setupDialogFilters(dialogView);
        
        // إعداد أزرار الإغلاق والتطبيق
        dialogView.findViewById(R.id.close_button).setOnClickListener(v -> filterDialog.dismiss());
        dialogView.findViewById(R.id.apply_filters).setOnClickListener(v -> {
            applyFilters();
            filterDialog.dismiss();
        });
        dialogView.findViewById(R.id.clear_filters).setOnClickListener(v -> clearAllFilters(dialogView));
        
        filterDialog.show();
    }

    private void setupDialogFilters(View dialogView) {
        // فلاتر البلد
        ChipGroup countryChips = dialogView.findViewById(R.id.country_chips);
        if (countryChips != null) {
            countryChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
                // Handle country filter changes
            });
        }

        // فلاتر النوع
        ChipGroup typeChips = dialogView.findViewById(R.id.type_chips);
        if (typeChips != null) {
            typeChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
                // Handle type filter changes
            });
        }

        // فلاتر التاريخ
        ChipGroup dateChips = dialogView.findViewById(R.id.date_chips);
        if (dateChips != null) {
            dateChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
                // Handle date filter changes
            });
        }

        // فلاتر الحالة
        ChipGroup statusChips = dialogView.findViewById(R.id.status_chips);
        if (statusChips != null) {
            statusChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
                // Handle status filter changes
            });
        }
    }

    private void clearAllFilters(View dialogView) {
        ChipGroup countryChips = dialogView.findViewById(R.id.country_chips);
        ChipGroup typeChips = dialogView.findViewById(R.id.type_chips);
        ChipGroup dateChips = dialogView.findViewById(R.id.date_chips);
        ChipGroup statusChips = dialogView.findViewById(R.id.status_chips);
        
        if (countryChips != null) countryChips.clearCheck();
        if (typeChips != null) typeChips.clearCheck();
        if (dateChips != null) dateChips.clearCheck();
        if (statusChips != null) statusChips.clearCheck();
    }

    private void applyFilters() {
        Toast.makeText(getContext(), "تم تطبيق الفلاتر", Toast.LENGTH_SHORT).show();
        // هنا يمكن إضافة منطق تطبيق الفلاتر
    }

    private void observeViewModel() {
        // Observe regular events
        viewModel.getEvents().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        if (resource.getData() != null && !resource.getData().isEmpty()) {
                            showEvents(resource.getData());
                        } else {
                            showEmptyState();
                        }
                        break;
                    case ERROR:
                        String errorMessage = resource.getMessage();
                        if (errorMessage == null || errorMessage.isEmpty()) {
                            errorMessage = "حدث خطأ في تحميل الفعاليات. تحقق من اتصال الإنترنت وحاول مرة أخرى.";
                        }
                        showError(errorMessage);
                        break;
                    case LOADING:
                        showLoading();
                        break;
                }
            } else {
                // Handle null resource
                showError("فشل في تحميل البيانات");
            }
        });
        
        // Observe featured event
        viewModel.getFeaturedEvent().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        if (resource.getData() != null) {
                            showFeaturedEvent(resource.getData());
                        } else {
                            hideFeaturedEvent();
                        }
                        break;
                    case ERROR:
                        // Don't show error for featured event, just hide it
                        hideFeaturedEvent();
                        break;
                    case LOADING:
                        // Keep current featured event while loading
                        break;
                }
            }
        });
    }

    private void showEvents(List<Event> events) {
        if (eventsRecyclerView != null) {
            eventsRecyclerView.setVisibility(View.VISIBLE);
        }
        
        View emptyStateLayout = rootView.findViewById(R.id.empty_state);
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(View.GONE);
        }
        
        View progressBar = rootView.findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        
        if (eventAdapter != null) {
            eventAdapter.updateEvents(events);
        }
        
        // إيقاف SwipeRefreshLayout
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showEmptyState() {
        if (eventsRecyclerView != null) {
            eventsRecyclerView.setVisibility(View.GONE);
        }
        
        View emptyStateLayout = rootView.findViewById(R.id.empty_state);
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
        
        View progressBar = rootView.findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        
        // إيقاف SwipeRefreshLayout
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showLoading() {
        View progressBar = rootView.findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        if (eventsRecyclerView != null) {
            eventsRecyclerView.setVisibility(View.GONE);
        }
        
        View emptyStateLayout = rootView.findViewById(R.id.empty_state);
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        View progressBar = rootView.findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        
        if (eventsRecyclerView != null) {
            eventsRecyclerView.setVisibility(View.GONE);
        }
        
        View emptyStateLayout = rootView.findViewById(R.id.empty_state);
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            
            // تحديث نص رسالة الخطأ
            TextView errorTitle = emptyStateLayout.findViewById(R.id.empty_state_title);
            TextView errorMessage = emptyStateLayout.findViewById(R.id.empty_state_message);
            
            if (errorTitle != null) {
                errorTitle.setText("خطأ في التحميل");
            }
            if (errorMessage != null) {
                errorMessage.setText(message);
            }
        }
        
        // إيقاف SwipeRefreshLayout
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        
        // عرض رسالة خطأ قصيرة
        if (getContext() != null) {
            Toast.makeText(getContext(), "فشل في تحميل الفعاليات", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFeaturedEvent(Event event) {
        if (featuredEventLayout != null) {
            featuredEventLayout.setVisibility(View.VISIBLE);
            
            if (featuredEventTitle != null) {
                featuredEventTitle.setText(event.getTitle());
            }
            
            if (featuredEventDescription != null) {
                featuredEventDescription.setText(event.getDescription());
            }
            
            if (featuredEventDate != null) {
                featuredEventDate.setText(event.getFormattedDate());
            }
            
            if (featuredEventLocation != null) {
                featuredEventLocation.setText(event.getLocation());
            }
            
            if (featuredEventImage != null && event.getImageUrl() != null) {
                // Load image using Glide or similar library
                // Glide.with(this).load(event.getImageUrl()).into(featuredEventImage);
            }
        }
    }
    
    private void hideFeaturedEvent() {
        if (featuredEventLayout != null) {
            featuredEventLayout.setVisibility(View.GONE);
        }
    }

    private void onEventClick(Event event) {
        Intent intent = EventDetailsActivity.createIntent(getContext(), event);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // تحديث البيانات عند العودة للصفحة
        if (viewModel != null) {
            viewModel.refreshEvents();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // تنظيف الموارد
        if (filterDialog != null && filterDialog.isShowing()) {
            filterDialog.dismiss();
        }
    }
}