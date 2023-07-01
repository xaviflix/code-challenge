package com.example.template.services;

import com.example.template.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class SessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionService.class);
    private final SessionRepository sessionRepository;
    private final EventRepository eventRepository;
    private final MachineRepository machineRepository;

    public SessionService(SessionRepository sessionRepository, EventRepository eventRepository,
                          MachineRepository machineRepository) {
        this.sessionRepository = sessionRepository;
        this.eventRepository = eventRepository;
        this.machineRepository = machineRepository;
    }

    public void processSession(Session session) {
        try {
            Session onGoingSession = sessionRepository.getMachineLastOnGoingSession(session.getMachineId());
            if (onGoingSession != null) {
                _closeSession(onGoingSession);
            }
            sessionRepository.save(session);
            machineRepository.saveIfNotExists(session.getMachineId());
        } catch (Exception e) {
            LOGGER.error("Exception e : {}", e.getMessage());
        }
    }

    private void _closeSession(Session onGoingSession) {
        // Instead of using the system time, get the timestamp of the last event received for the
        // session in order to close it
        Event lastEvent = eventRepository.getLastSessionEvent(onGoingSession.getSessionId());
        Timestamp finishAt = lastEvent == null ? Timestamp.from(Instant.now()) : lastEvent.getEventAt();
        onGoingSession.setFinishAt(finishAt);
        sessionRepository.save(onGoingSession);
    }
}
