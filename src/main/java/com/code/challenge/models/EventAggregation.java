package com.code.challenge.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table( name="eventaggregations",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "sessionId", "eventType" }) },
        indexes = { @Index(name = "sessionEventTypeIdx", columnList = "sessionId, eventType") }
)
public class EventAggregation {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  private String sessionId;

  private String eventType;

  private BigDecimal aggregatedValue;

  private Timestamp updateAt;

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


  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public BigDecimal getAggregatedValue() {
    return aggregatedValue;
  }

  public void setAggregatedValue(BigDecimal aggregatedValue) {
    this.aggregatedValue = aggregatedValue;
  }

  public Timestamp getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Timestamp updateAt) {
    this.updateAt = updateAt;
  }

  public static EventAggregation create(String sessionId, String eventType, BigDecimal eventValue, Timestamp eventAt) {
    EventAggregation eventAggregation = new EventAggregation();
    eventAggregation.setSessionId(sessionId);
    eventAggregation.setEventType(eventType);
    eventAggregation.setAggregatedValue(eventValue);
    eventAggregation.setUpdateAt(eventAt);
    return eventAggregation;
  }

  public void update(BigDecimal eventValue, Timestamp eventAt) {
    setAggregatedValue(aggregatedValue.add(eventValue));
    setUpdateAt(eventAt);
  }
}