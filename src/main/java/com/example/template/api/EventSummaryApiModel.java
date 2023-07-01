package com.example.template.api;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record EventSummaryApiModel(String eventType, BigDecimal eventValue, Timestamp lastUpdatedAt) { }