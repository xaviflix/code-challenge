package com.example.template.services;

import com.example.template.models.Event;
import com.example.template.models.EventAggregation;
import com.example.template.models.EventAggregationRepository;
import com.example.template.models.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
