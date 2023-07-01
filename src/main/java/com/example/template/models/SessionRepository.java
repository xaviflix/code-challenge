package com.example.template.models;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SessionRepository extends CrudRepository<Session, Integer> {

    List<Session> findByMachineIdAndFinishAtIsNull(String sessionId);
    Session findFirstByMachineIdOrderByIdDesc(String sessionId);
    Session findFirstBySessionIdAndMachineId(String sessionId, String machineId);

    default Session getMachineLastOnGoingSession(String machineId) {
        List <Session> sessionList = findByMachineIdAndFinishAtIsNull(machineId);
        if (sessionList.isEmpty()) {
            return null;
        }
        else {
            // Due to application design, only one session could be opened at the same time
            return sessionList.get(0);
        }
    }

    default Session getExistingSession(String sessionId, String machineId) {
        try {
            return findFirstBySessionIdAndMachineId(sessionId, machineId);
        } catch(EntityNotFoundException notFoundError) {
            return null;
        }
    }

    default Session getLastSession(String machineId) {
        try {
            return findFirstByMachineIdOrderByIdDesc(machineId);
        } catch(EntityNotFoundException notFoundError) {
            return null;
        }
    }
}