package com.example.template.api;

import java.util.List;

public record SessionSummaryApiModel(String sessionId, String machineId, List<EventSummaryApiModel> eventSummary) { }