package com.code.challenge.api;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record EventSummaryApiModel(String eventType, BigDecimal eventValue, Timestamp lastUpdatedAt) { }