package com.example.template.api;

import com.example.template.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/machines")
public class MachineController {
    @Autowired
    private MachineRepository machineRepository;

    @GetMapping(path="/list")
    public List<MachineApiModel> getAll() {
        Iterable<Machine> machinesList = machineRepository.findAll();
        List<MachineApiModel> machinesApi = new ArrayList();
        machinesList.forEach(machine -> {
            machinesApi.add(new MachineApiModel(machine.getMachineId()));
        });
        return machinesApi;
    }
}