package com.example.template.models;

import jakarta.persistence.*;

@Entity
@Table( name="machines",
        indexes = { @Index(name = "machineIdIdx", columnList = "machineId") }
)
public class Machine {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  private String machineId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getMachineId() {
    return machineId;
  }

  public void setMachineId(String machineId) {
    this.machineId = machineId;
  }

  public static Machine create(String machineId) {
    Machine machine = new Machine();
    machine.setMachineId(machineId);
    return machine;
  }
}