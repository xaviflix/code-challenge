package com.example.template.services;

import com.example.template.models.Event;
import com.example.template.models.EventRepository;
import com.example.template.models.Session;
import com.example.template.models.SessionRepository;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.template.utils.JsonMessageParser.parseJsonMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SessionServiceTests {

    private SessionService sessionService;
    SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
    EventRepository eventRepository = Mockito.mock(EventRepository.class);

    @BeforeEach
    void before() {
        sessionService = new SessionService(sessionRepository, eventRepository);
    }

    @Test
    void when_process_new_session_without_previous_session() {
        // Mocks
        when(sessionRepository.getMachineLastOnGoingSession(any(String.class))).thenCallRealMethod();
        when(sessionRepository.findByMachineIdAndFinishAtIsNull(any(String.class))).thenReturn(new ArrayList<Session>());

        // When
        Session session = Session.create(
                "session-id-value", "machine-id-value", new Timestamp(1688057249));
        sessionService.processSession(session);

        // Then
        verify(eventRepository, never()).getLastSessionEvent(any(String.class));
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void when_process_new_session_with_previous_session() {
        // Mocks
        Session prevSession = Session.create(
                "prev-session-id-value", "prev-machine-id-value", new Timestamp(1688057249));
        Event prevEvent = Event.create(
                "prev-session-id-value", "consumedFuel", new BigDecimal(1.0), new Timestamp(1688017249));
        when(sessionRepository.getMachineLastOnGoingSession(any(String.class))).thenCallRealMethod();
        when(sessionRepository.findByMachineIdAndFinishAtIsNull(any(String.class))).thenReturn(List.of(prevSession));
        when(eventRepository.getLastSessionEvent(any(String.class))).thenCallRealMethod();
        when(eventRepository.findFirstBySessionIdOrderByIdDesc(any(String.class))).thenReturn(prevEvent);

        // When
        Session session = Session.create(
                "session-id-value", "machine-id-value", new Timestamp(1688057249));
        sessionService.processSession(session);

        // Then
        verify(eventRepository, times(1)).getLastSessionEvent(any(String.class));
        verify(sessionRepository, times(1)).save(prevSession);
        verify(sessionRepository, times(1)).save(session);
        assertEquals(prevEvent.getEventAt(), prevSession.getFinishAt());
    }
}
