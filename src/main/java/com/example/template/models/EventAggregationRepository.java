package com.example.template.models;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventAggregationRepository extends CrudRepository<EventAggregation, Integer> {

    EventAggregation findBySessionIdAndEventTypeOrderByIdDesc(String sessionId, String eventType);

    public default EventAggregation getSessionEventAggregation(String sessionId, String eventType) {
        try {
            return findBySessionIdAndEventTypeOrderByIdDesc(sessionId, eventType);
        } catch(EntityNotFoundException notFoundError) {
            return null;
        }
    }

}