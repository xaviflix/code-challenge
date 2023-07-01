package com.example.template.api;

import com.example.template.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/sessions")
public class SessionController {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private EventAggregationRepository eventAggregationRepository;
    @GetMapping(path="/summary")
    public SessionSummaryApiModel getSessionSummary(@RequestParam String machineId, @RequestParam String sessionId) {

        Session session = sessionRepository.getExistingSession(sessionId, machineId);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
        } else {
            List<EventAggregation> eventAggregationList = eventAggregationRepository.findBySessionId(sessionId);
            List<EventSummaryApiModel> eventsSummary = new ArrayList();
            eventAggregationList.forEach(eventAggregation -> {
                eventsSummary.add(new EventSummaryApiModel(
                        eventAggregation.getEventType(),
                        eventAggregation.getAggregatedValue(),
                        eventAggregation.getUpdateAt())
                );
            });
            return new SessionSummaryApiModel(sessionId, machineId, eventsSummary);
        }
    }

    @GetMapping(path="/last")
    public SessionApiModel getSession(@RequestParam String machineId) {

        Session session = sessionRepository.getLastSession(machineId);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
        } else {
            return new SessionApiModel(
                    session.getSessionId(),
                    session.getMachineId(),
                    session.getStartAt(),
                    session.getFinishAt());
        }
    }
}