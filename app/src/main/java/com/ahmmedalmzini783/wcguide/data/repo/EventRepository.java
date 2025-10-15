package com.ahmmedalmzini783.wcguide.data.repo;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.ahmmedalmzini783.wcguide.data.local.AppDatabase;
import com.ahmmedalmzini783.wcguide.data.local.dao.EventDao;
import com.ahmmedalmzini783.wcguide.data.local.entity.EventEntity;
import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.ahmmedalmzini783.wcguide.data.remote.FirebaseDataSource;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository {
    private final EventDao eventDao;
    private final FirebaseDataSource firebaseDataSource;
    private final ExecutorService executor;

    public EventRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        eventDao = database.eventDao();
        firebaseDataSource = new FirebaseDataSource();
        executor = Executors.newFixedThreadPool(4);
    }

    // Cache-then-Network strategy
    public LiveData<Resource<List<Event>>> getAllEvents() {
        MediatorLiveData<Resource<List<Event>>> result = new MediatorLiveData<>();

        // First, load from cache
        LiveData<List<EventEntity>> localData = eventDao.getAllEvents();
        result.addSource(localData, entities -> {
            if (entities != null && !entities.isEmpty()) {
                // Convert entities to models
                executor.execute(() -> {
                    List<Event> events = convertEntitiesToEvents(entities);
                    result.postValue(Resource.success(events));
                });
            }
        });

        // Then, fetch from network
        LiveData<Resource<List<Event>>> remoteData = firebaseDataSource.getAllEvents();
        result.addSource(remoteData, resource -> {
            if (resource != null) {
                if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                    // Update local cache
                    executor.execute(() -> {
                        List<EventEntity> entities = convertEventsToEntities(resource.getData());
                        if (entities != null && !entities.isEmpty()) {
                            eventDao.insertEvents(entities);
                        }
                    });
                    result.setValue(resource);
                } else if (resource.getStatus() == Resource.Status.ERROR) {
                    // If network fails, keep showing cached data
                    result.setValue(resource);
                }
            }
        });

        return result;
    }

    public LiveData<Resource<Event>> getEventById(String eventId) {
        MediatorLiveData<Resource<Event>> result = new MediatorLiveData<>();

        // First, load from cache
        LiveData<EventEntity> localData = eventDao.getEventById(eventId);
        result.addSource(localData, entity -> {
            if (entity != null) {
                Event event = convertEntityToEvent(entity);
                result.setValue(Resource.success(event));
            }
        });

        // Then, fetch from network
        LiveData<Resource<Event>> remoteData = firebaseDataSource.getEventById(eventId);
        result.addSource(remoteData, resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                // Update local cache
                executor.execute(() -> {
                    EventEntity entity = convertEventToEntity(resource.getData());
                    eventDao.insertEvent(entity);
                });
                result.setValue(resource);
            }
        });

        return result;
    }

    public LiveData<Resource<List<Event>>> getEventsByCountry(String country) {
        MediatorLiveData<Resource<List<Event>>> result = new MediatorLiveData<>();

        // Load from cache
        LiveData<List<EventEntity>> localData = eventDao.getEventsByCountry(country);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Event> events = convertEntitiesToEvents(entities);
                    result.postValue(Resource.success(events));
                });
            }
        });

        // Fetch from network
        LiveData<Resource<List<Event>>> remoteData = firebaseDataSource.getEventsByCountry(country);
        result.addSource(remoteData, resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                executor.execute(() -> {
                    List<EventEntity> entities = convertEventsToEntities(resource.getData());
                    if (entities != null && !entities.isEmpty()) {
                        eventDao.insertEvents(entities);
                    }
                });
                result.setValue(resource);
            }
        });

        return result;
    }

    public LiveData<Resource<List<Event>>> getEventsByCity(String city) {
        MediatorLiveData<Resource<List<Event>>> result = new MediatorLiveData<>();

        LiveData<List<EventEntity>> localData = eventDao.getEventsByCity(city);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Event> events = convertEntitiesToEvents(entities);
                    result.postValue(Resource.success(events));
                });
            }
        });

        return result;
    }

    public LiveData<Resource<List<Event>>> getEventsByType(String type) {
        MediatorLiveData<Resource<List<Event>>> result = new MediatorLiveData<>();

        LiveData<List<EventEntity>> localData = eventDao.getEventsByType(type);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Event> events = convertEntitiesToEvents(entities);
                    result.postValue(Resource.success(events));
                });
            }
        });

        return result;
    }

    public LiveData<Resource<List<Event>>> getUpcomingEvents(int limit) {
        MediatorLiveData<Resource<List<Event>>> result = new MediatorLiveData<>();

        long currentTime = System.currentTimeMillis();
        LiveData<List<EventEntity>> localData = eventDao.getUpcomingEvents(currentTime, limit);
        result.addSource(localData, entities -> {
            if (entities != null) {
                executor.execute(() -> {
                    List<Event> events = convertEntitiesToEvents(entities);
                    result.postValue(Resource.success(events));
                });
            }
        });

        return result;
    }

    public LiveData<List<String>> getAllCountries() {
        return eventDao.getAllCountries();
    }

    public LiveData<List<String>> getCitiesByCountry(String country) {
        return eventDao.getCitiesByCountry(country);
    }

    public LiveData<List<String>> getAllTypes() {
        return eventDao.getAllTypes();
    }

    public LiveData<Resource<Event>> getFeaturedEvent() {
        MediatorLiveData<Resource<Event>> result = new MediatorLiveData<>();

        // First, load from cache
        LiveData<EventEntity> localData = eventDao.getFeaturedEvent();
        result.addSource(localData, entity -> {
            if (entity != null) {
                Event event = convertEntityToEvent(entity);
                result.setValue(Resource.success(event));
            }
        });

        // Then, fetch from network
        LiveData<Resource<Event>> remoteData = firebaseDataSource.getFeaturedEvent();
        result.addSource(remoteData, resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                // Update local cache
                executor.execute(() -> {
                    EventEntity entity = convertEventToEntity(resource.getData());
                    eventDao.insertEvent(entity);
                });
                result.setValue(resource);
            }
        });

        return result;
    }

    // Admin methods
    public LiveData<Resource<Void>> addEvent(Event event) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        firebaseDataSource.addEvent(event)
                .addOnSuccessListener(aVoid -> {
                    // Also add to local cache
                    executor.execute(() -> {
                        EventEntity entity = convertEventToEntity(event);
                        eventDao.insertEvent(entity);
                    });
                    result.setValue(Resource.success(null));
                })
                .addOnFailureListener(e -> {
                    result.setValue(Resource.error(e.getMessage(), null));
                });

        return result;
    }

    // Conversion methods
    private List<Event> convertEntitiesToEvents(List<EventEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        
        List<Event> events = new ArrayList<>();
        for (EventEntity entity : entities) {
            if (entity != null) {
                events.add(convertEntityToEvent(entity));
            }
        }
        return events;
    }

    private Event convertEntityToEvent(EventEntity entity) {
        Event event = new Event();
        event.setId(entity.getId());
        event.setTitle(entity.getTitle());
        event.setCountry(entity.getCountry());
        event.setCity(entity.getCity());
        event.setVenueName(entity.getVenueName());
        event.setType(entity.getType());
        event.setStartUtc(entity.getStartUtc());
        event.setEndUtc(entity.getEndUtc());
        event.setImageUrl(entity.getImageUrl());
        event.setCapacity(entity.getCapacity());
        event.setTicketUrl(entity.getTicketUrl());
        event.setDescription(entity.getDescription());
        event.setLat(entity.getLat());
        event.setLng(entity.getLng());
        event.setFeatured(entity.isFeatured());
        return event;
    }

    private List<EventEntity> convertEventsToEntities(List<Event> events) {
        if (events == null) {
            return new ArrayList<>();
        }
        
        List<EventEntity> entities = new ArrayList<>();
        for (Event event : events) {
            if (event != null) {
                entities.add(convertEventToEntity(event));
            }
        }
        return entities;
    }

    private EventEntity convertEventToEntity(Event event) {
        EventEntity entity = new EventEntity();
        entity.setId(event.getId());
        entity.setTitle(event.getTitle());
        entity.setCountry(event.getCountry());
        entity.setCity(event.getCity());
        entity.setVenueName(event.getVenueName());
        entity.setType(event.getType());
        entity.setStartUtc(event.getStartUtc());
        entity.setEndUtc(event.getEndUtc());
        entity.setImageUrl(event.getImageUrl());
        entity.setCapacity(event.getCapacity());
        entity.setTicketUrl(event.getTicketUrl());
        entity.setDescription(event.getDescription());
        entity.setLat(event.getLat());
        entity.setLng(event.getLng());
        entity.setFeatured(event.isFeatured());
        entity.setLastUpdated(System.currentTimeMillis());
        return entity;
    }
}