package com.ahmmedalmzini783.wcguide.ui.events;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.ahmmedalmzini783.wcguide.data.repo.EventRepository;
import com.ahmmedalmzini783.wcguide.data.repo.UserRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class EventsViewModel extends AndroidViewModel {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<FilterState> filterState = new MutableLiveData<>(new FilterState());
    private final MediatorLiveData<Resource<List<Event>>> events = new MediatorLiveData<>();

    public EventsViewModel(@NonNull Application application) {
        super(application);
        eventRepository = new EventRepository(application);
        userRepository = new UserRepository(application);
    }

    public void loadEvents() {
        FilterState currentFilter = filterState.getValue();
        if (currentFilter != null) {
            LiveData<Resource<List<Event>>> source;

            if (currentFilter.hasActiveFilters()) {
                // Apply filters
                if (currentFilter.getSelectedCountry() != null) {
                    source = eventRepository.getEventsByCountry(currentFilter.getSelectedCountry());
                } else if (currentFilter.getSelectedCity() != null) {
                    source = eventRepository.getEventsByCity(currentFilter.getSelectedCity());
                } else if (currentFilter.getSelectedType() != null) {
                    source = eventRepository.getEventsByType(currentFilter.getSelectedType());
                } else {
                    source = eventRepository.getAllEvents();
                }
            } else {
                source = eventRepository.getAllEvents();
            }

            events.addSource(source, events::setValue);
        }
    }

    public void refreshEvents() {
        loadEvents();
    }

    public void setCountryFilter(String country) {
        FilterState current = filterState.getValue();
        if (current != null) {
            current.setSelectedCountry(country);
            filterState.setValue(current);
            loadEvents();
        }
    }

    public void setCityFilter(String city) {
        FilterState current = filterState.getValue();
        if (current != null) {
            current.setSelectedCity(city);
            filterState.setValue(current);
            loadEvents();
        }
    }

    public void setTypeFilter(String type) {
        FilterState current = filterState.getValue();
        if (current != null) {
            current.setSelectedType(type);
            filterState.setValue(current);
            loadEvents();
        }
    }

    public void setDateRangeFilter(long startDate, long endDate) {
        FilterState current = filterState.getValue();
        if (current != null) {
            current.setSelectedDateRange(new DateRange(startDate, endDate));
            filterState.setValue(current);
            loadEvents();
        }
    }

    public void clearFilters() {
        filterState.setValue(new FilterState());
        loadEvents();
    }

    public void toggleFavorite(String eventId, String targetKind, boolean isFavorite) {
        // TODO: Implement favorite toggle
        // if (userRepository.getCurrentUser() != null) {
        //     String userId = userRepository.getCurrentUser().getUid();
        //     if (isFavorite) {
        //         userRepository.addToFavorites(userId, eventId, targetKind);
        //     } else {
        //         userRepository.removeFromFavorites(userId, eventId, targetKind);
        //     }
        // }
    }

    public LiveData<Resource<List<Event>>> getEvents() {
        return events;
    }

    public LiveData<FilterState> getFilterState() {
        return filterState;
    }

    // Filter state class
    public static class FilterState {
        private String selectedCountry;
        private String selectedCity;
        private String selectedType;
        private DateRange selectedDateRange;

        public boolean hasActiveFilters() {
            return selectedCountry != null || selectedCity != null ||
                    selectedType != null || selectedDateRange != null;
        }

        // Getters and setters
        public String getSelectedCountry() { return selectedCountry; }
        public void setSelectedCountry(String selectedCountry) { this.selectedCountry = selectedCountry; }

        public String getSelectedCity() { return selectedCity; }
        public void setSelectedCity(String selectedCity) { this.selectedCity = selectedCity; }

        public String getSelectedType() { return selectedType; }
        public void setSelectedType(String selectedType) { this.selectedType = selectedType; }

        public DateRange getSelectedDateRange() { return selectedDateRange; }
        public void setSelectedDateRange(DateRange selectedDateRange) { this.selectedDateRange = selectedDateRange; }
    }

    // Date range class
    public static class DateRange {
        private final long startDate;
        private final long endDate;

        public DateRange(long startDate, long endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public long getStartDate() { return startDate; }
        public long getEndDate() { return endDate; }
    }
}