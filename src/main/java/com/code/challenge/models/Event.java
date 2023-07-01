package com.code.challenge.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table( name="events",
        indexes = { @Index(name = "sessionEventTypeIdx", columnList = "sessionId, eventType") }
)
public class Event {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  private String sessionId;

  private Timestamp eventAt;

  private String eventType;

  private BigDecimal eventValue;

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

  public Timestamp getEventAt() {
    return eventAt;
  }

  public void setEventAt(Timestamp eventAt) {
    this.eventAt = eventAt;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public BigDecimal getEventValue() {
    return eventValue;
  }

  public void setEventValue(BigDecimal eventValue) {
    this.eventValue = eventValue;
  }

  public static Event create(String sessionId, String eventType, BigDecimal eventValue, Timestamp eventAt) {
    Event event = new Event();
    event.setSessionId(sessionId);
    event.setEventType(eventType);
    event.setEventValue(eventValue);
    event.setEventAt(eventAt);
    return event;
  }
}