package com.code.challenge.models;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventAggregationRepository extends CrudRepository<EventAggregation, Integer> {

    // There is only one single register by sessionId/eventType by table definition
    EventAggregation findFirstBySessionIdAndEventType(String sessionId, String eventType);

    List<EventAggregation> findBySessionId(String sessionId);
    public default EventAggregation getSessionEventAggregation(String sessionId, String eventType) {
        try {
            return findFirstBySessionIdAndEventType(sessionId, eventType);
        } catch(EntityNotFoundException notFoundError) {
            return null;
        }
    }

}