package com.code.challenge.services;

import com.code.challenge.models.Event;
import com.code.challenge.models.EventAggregation;
import com.code.challenge.models.EventAggregationRepository;
import com.code.challenge.models.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;
    private final EventAggregationRepository eventAggregationRepository;

    public EventService(EventRepository eventRepository, EventAggregationRepository eventAggregationRepository) {
        this.eventRepository = eventRepository;
        this.eventAggregationRepository = eventAggregationRepository;
    }

    public void processEvent(Event event) {
        try {
            eventRepository.save(event);
            EventAggregation eventAggregation =
                    eventAggregationRepository.getSessionEventAggregation(event.getSessionId(), event.getEventType());
            if (eventAggregation == null) {
                eventAggregation = EventAggregation.create(
                        event.getSessionId(), event.getEventType(), event.getEventValue(), event.getEventAt());
            }
            else {
                eventAggregation.update(event.getEventValue(), event.getEventAt());
            }
            eventAggregationRepository.save(eventAggregation);
        } catch (Exception e) {
            LOGGER.error("Exception e : {}", e.getMessage());
        }
    }
}
