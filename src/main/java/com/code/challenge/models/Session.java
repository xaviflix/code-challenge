package com.code.challenge.models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table( name="sessions",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "sessionId", "machineId" }) },
        indexes = { @Index(name = "sessionMachineIdx", columnList = "sessionId, machineId", unique = true),
                    @Index(name = "machineIdx", columnList = "machineId") }
)
public class Session {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  private String sessionId;

  private String machineId;

  private Timestamp startAt;

  private Timestamp finishAt;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getMachineId() {
    return machineId;
  }

  public void setMachineId(String machineId) {
    this.machineId = machineId;
  }

  public Timestamp getStartAt() {
    return startAt;
  }

  public void setStartAt(Timestamp startAt) {
    this.startAt = startAt;
  }

  public Timestamp getFinishAt() {
    return finishAt;
  }

  public void setFinishAt(Timestamp finishAt) {
    this.finishAt = finishAt;
  }

  public static Session create(String sessionId, String machineId, Timestamp startAt) {
    Session session = new Session();
    session.setSessionId(sessionId);
    session.setMachineId(machineId);
    session.setStartAt(startAt);
    return session;
  }
}