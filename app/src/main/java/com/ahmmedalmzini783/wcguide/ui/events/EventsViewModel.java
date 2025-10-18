package com.ahmmedalmzini783.wcguide.ui.events;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.ahmmedalmzini783.wcguide.data.repo.EventRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.List;

public class EventsViewModel extends AndroidViewModel {

    private final EventRepository eventRepository;
    private final LiveData<Resource<List<Event>>> events;
    private final LiveData<Resource<Event>> featuredEvent;

    public EventsViewModel(Application application) {
        super(application);
        eventRepository = new EventRepository(application);
        events = eventRepository.getAllEvents();
        featuredEvent = eventRepository.getFeaturedEvent();
    }

    public LiveData<Resource<List<Event>>> getEvents() {
        return events;
    }

    public LiveData<Resource<Event>> getFeaturedEvent() {
        return featuredEvent;
    }

    public void loadEvents() {
        // Force refresh events from Firebase
        // This will trigger the repository to fetch fresh data
        eventRepository.getAllEvents();
        eventRepository.getFeaturedEvent();
    }

    public void refreshEvents() {
        // Force a complete refresh of events
        loadEvents();
    }
}