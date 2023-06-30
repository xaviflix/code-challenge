package com.example.template.models;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SessionRepository extends CrudRepository<Session, Integer> {

    List<Session> findByMachineIdAndFinishAtIsNull(String sessionId);

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
}