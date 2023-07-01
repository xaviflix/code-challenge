package com.code.challenge.api;

import java.util.List;

public record SessionSummaryApiModel(String sessionId, String machineId, List<EventSummaryApiModel> eventSummary) { }