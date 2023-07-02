package com.code.challenge.controllers;

import com.code.challenge.tasks.ReadMessagesTask;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SessionControllerTests extends ControllerTests {

	@Test
	void when_no_session_exist_returns_404_not_found_error() {
		String resultAsJson = apiGetRequest("/sessions/summary?sessionId=session-1&machineId=machine-1");
		assertTrue(resultAsJson.contains("\"status\":404"));
		assertTrue(resultAsJson.contains("\"error\":\"Not Found\""));

		resultAsJson = apiGetRequest("/sessions/last?machineId=machine-1");
		assertTrue(resultAsJson.contains("\"status\":404"));
		assertTrue(resultAsJson.contains("\"error\":\"Not Found\""));
	}

	@Test
	void when_session_and_events_exists_returns_correct_data() throws Exception{
		String session1 = "{\"sessionId\": \"session-1\", \"machineId\": \"machine-1\", \"startAt\": 1688114297}";
		String session2 = "{\"sessionId\": \"session-2\", \"machineId\": \"machine-2\", \"startAt\": 1688121497}";
		String eventsSession1 = "{\"sessionId\": \"session-1\", \"events\": [{\"eventAt\": 1688114298, \"eventType\": \"drivenDistance\", \"numericEventValue\": 11.22},{\"eventAt\": 1688114299, \"eventType\": \"drivenDistance\", \"numericEventValue\": 11.2209},{\"eventAt\": 1688114300, \"eventType\": \"consumedFuel\", \"numericEventValue\": 0.127}]}";
		String eventsSession2 = "{\"sessionId\": \"session-2\", \"events\": [{\"eventAt\": 1688121498, \"eventType\": \"consumedFuel\", \"numericEventValue\": 1.23},{\"eventAt\": 1688121499, \"eventType\": \"drivenDistance\", \"numericEventValue\": 9.876},{\"eventAt\": 1688121500, \"eventType\": \"consumedFuel\", \"numericEventValue\": 4.56}]}";
		String session3 = "{\"sessionId\": \"session-3\", \"machineId\": \"machine-1\", \"startAt\": 1688114301}";
		sqsService.publishMessage("events_queue", session1);
		sqsService.publishMessage("events_queue", session2);
		sqsService.publishMessage("events_queue", eventsSession1);
		sqsService.publishMessage("events_queue", eventsSession2);
		sqsService.publishMessage("events_queue", session3);

		sleep(ReadMessagesTask.READ_MESSAGES_TASK_INITIAL_DELAY_MS + 2*ReadMessagesTask.READ_MESSAGES_TASK_INITIAL_DELAY_MS);
		String resultAsJson = null;

		resultAsJson = apiGetRequest("/sessions/summary?sessionId=session-1&machineId=machine-1");
		assertEquals("{\"sessionId\":\"session-1\",\"machineId\":\"machine-1\",\"eventSummary\":[{\"eventType\":\"consumedFuel\",\"eventValue\":0.13,\"lastUpdatedAt\":\"2023-06-30T08:38:20.000+00:00\"},{\"eventType\":\"drivenDistance\",\"eventValue\":22.44,\"lastUpdatedAt\":\"2023-06-30T08:38:19.000+00:00\"}]}", resultAsJson);

		resultAsJson = apiGetRequest("/sessions/summary?sessionId=session-2&machineId=machine-2");
		assertEquals("{\"sessionId\":\"session-2\",\"machineId\":\"machine-2\",\"eventSummary\":[{\"eventType\":\"consumedFuel\",\"eventValue\":5.79,\"lastUpdatedAt\":\"2023-06-30T10:38:20.000+00:00\"},{\"eventType\":\"drivenDistance\",\"eventValue\":9.88,\"lastUpdatedAt\":\"2023-06-30T10:38:19.000+00:00\"}]}", resultAsJson);

		resultAsJson = apiGetRequest("/sessions/summary?sessionId=session-3&machineId=machine-1");
		assertEquals("{\"sessionId\":\"session-3\",\"machineId\":\"machine-1\",\"eventSummary\":[]}", resultAsJson);

		resultAsJson = apiGetRequest("/sessions/summary?sessionId=session-1&machineId=machine-2");
		assertTrue(resultAsJson.contains("\"status\":404"));
		assertTrue(resultAsJson.contains("\"error\":\"Not Found\""));

		resultAsJson = apiGetRequest("/sessions/last?machineId=machine-1");
		assertEquals("{\"sessionId\":\"session-3\",\"machineId\":\"machine-1\",\"startedAt\":\"2023-06-30T08:38:21.000+00:00\",\"finishedAt\":null}", resultAsJson);

		resultAsJson = apiGetRequest("/sessions/last?machineId=machine-2");
		assertEquals("{\"sessionId\":\"session-2\",\"machineId\":\"machine-2\",\"startedAt\":\"2023-06-30T10:38:17.000+00:00\",\"finishedAt\":null}", resultAsJson);

		resultAsJson = apiGetRequest("/sessions/last?machineId=machine-3");
		assertTrue(resultAsJson.contains("\"status\":404"));
		assertTrue(resultAsJson.contains("\"error\":\"Not Found\""));
	}
}
