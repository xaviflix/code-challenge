package com.code.challenge.models;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Integer> {

    Event findFirstBySessionIdOrderByIdDesc(String sessionId);

    default Event getLastSessionEvent(String sessionId) {
        try {
            return findFirstBySessionIdOrderByIdDesc(sessionId);
        } catch(EntityNotFoundException notFoundError) {
            return null;
        }
    }

}