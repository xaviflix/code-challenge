package com.example.template.api;

import java.sql.Timestamp;

public record SessionApiModel(String sessionId, String machineId, Timestamp startedAt, Timestamp finishedAt) { }