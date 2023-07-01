package com.code.challenge.api;

import java.sql.Timestamp;

public record SessionApiModel(String sessionId, String machineId, Timestamp startedAt, Timestamp finishedAt) { }