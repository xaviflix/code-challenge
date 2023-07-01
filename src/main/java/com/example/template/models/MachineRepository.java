package com.example.template.models;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

public interface MachineRepository extends CrudRepository<Machine, Integer> {

    // Only one item can be returned due to table definition
    Machine findFirstByMachineId(String machineId);

    default boolean saveIfNotExists(String machineId) {
        if (findFirstByMachineId(machineId) != null) {
            return false;
        }
        else {
            save(Machine.create(machineId));
            return true;
        }
    }

}