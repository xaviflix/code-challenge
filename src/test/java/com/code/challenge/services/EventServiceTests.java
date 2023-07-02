package com.code.challenge.services;

import com.code.challenge.models.Event;
import com.code.challenge.models.EventAggregation;
import com.code.challenge.models.EventAggregationRepository;
import com.code.challenge.models.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventServiceTests {

    private EventService eventService;
    private EventAggregationRepository eventAggregationRepository = Mockito.mock(EventAggregationRepository.class);
    private EventRepository eventRepository = Mockito.mock(EventRepository.class);

    @BeforeEach
    void before() {
        eventService = new EventService(eventRepository, eventAggregationRepository);
    }

    @Test
    void when_process_new_event_without_previous_aggregation() {
        // Mocks
        when(eventAggregationRepository.getSessionEventAggregation(any(String.class), any(String.class))).thenCallRealMethod();
        when(eventAggregationRepository.findFirstBySessionIdAndEventType(any(String.class), any(String.class))).thenThrow(new EntityNotFoundException());

        // When
        Event event = Event.create(
                "session-id-value", "event-type-value", new BigDecimal("11.22"), new Timestamp(1688057249));
        eventService.processEvent(event);

        // Then
        verify(eventAggregationRepository, times(1)).save(any(EventAggregation.class));
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void when_process_new_event_with_previous_aggregation() {
        // Mocks
        EventAggregation existingEventAggregation = EventAggregation.create(
                "session-id-value", "event-type-value", new BigDecimal("10.201"), new Timestamp(1686057249));
        when(eventAggregationRepository.getSessionEventAggregation(any(String.class), any(String.class))).thenCallRealMethod();
        when(eventAggregationRepository.findFirstBySessionIdAndEventType(any(String.class), any(String.class))).thenReturn(existingEventAggregation);

        // When
        Event event = Event.create(
                "session-id-value", "event-type-value", new BigDecimal("11.2233"), new Timestamp(1688057249));
        eventService.processEvent(event);

        // Then
        verify(eventAggregationRepository, times(1)).save(existingEventAggregation);
        verify(eventRepository, times(1)).save(event);
        assertEquals(existingEventAggregation.getUpdateAt(), event.getEventAt());
        assertEquals(new BigDecimal("21.4243"), existingEventAggregation.getAggregatedValue());
    }
}
