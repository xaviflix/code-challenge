package com.example.template.services;

import com.example.template.models.Event;
import com.example.template.models.EventRepository;
import com.example.template.models.Session;
import com.example.template.models.SessionRepository;
import com.example.template.models.Machine;
import com.example.template.models.MachineRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SessionServiceTests {

    private SessionService sessionService;
    private SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
    private EventRepository eventRepository = Mockito.mock(EventRepository.class);

    private MachineRepository machineRepository = Mockito.mock(MachineRepository.class);

    @BeforeEach
    void before() {
        sessionService = new SessionService(sessionRepository, eventRepository, machineRepository);
    }

    @Test
    void when_process_new_session_without_previous_session() {
        // Mocks
        when(sessionRepository.getMachineLastOnGoingSession(any(String.class))).thenCallRealMethod();
        when(sessionRepository.findByMachineIdAndFinishAtIsNull(any(String.class))).thenReturn(new ArrayList<Session>());
        when(machineRepository.saveIfNotExists(any(String.class))).thenCallRealMethod();
        when(machineRepository.findFirstByMachineId(any(String.class))).thenReturn(null);

        // When
        Session session = Session.create(
                "session-id-value", "machine-id-value", new Timestamp(1688057249));
        sessionService.processSession(session);

        // Then
        verify(eventRepository, never()).getLastSessionEvent(any(String.class));
        verify(sessionRepository, times(1)).save(session);
        verify(machineRepository, times(1)).save(any(Machine.class));
    }

    @Test
    void when_process_new_session_with_previous_session() {
        // Mocks
        Session prevSession = Session.create(
                "prev-session-id-value", "machine-id-value", new Timestamp(1688057249));
        Event prevEvent = Event.create(
                "prev-session-id-value", "consumedFuel", new BigDecimal(1.0), new Timestamp(1688017249));
        when(sessionRepository.getMachineLastOnGoingSession(any(String.class))).thenCallRealMethod();
        when(sessionRepository.findByMachineIdAndFinishAtIsNull(any(String.class))).thenReturn(List.of(prevSession));
        when(machineRepository.saveIfNotExists(any(String.class))).thenCallRealMethod();
        when(machineRepository.findFirstByMachineId(any(String.class))).thenReturn(Machine.create("machine-id-value"));
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
        verify(machineRepository, never()).save(any(Machine.class));
        assertEquals(prevEvent.getEventAt(), prevSession.getFinishAt());
    }
}
