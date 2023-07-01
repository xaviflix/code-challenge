package com.code.challenge.controllers;

import com.code.challenge.tasks.ReadMessagesTask;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MachineControllerTests extends ControllerTests {

	@Test
	void when_no_machine_exist_returns_empty_list() {
		String resultAsJson = apiGetRequest("/machines/list");
		assertEquals("[]", resultAsJson);
	}

	@Test
	void when_several_machines_exist_returns_machines_list() throws Exception{
		String session1 = "{\"sessionId\": \"session-1\", \"machineId\": \"machine-1\", \"startAt\": 1688057249}";
		String session2 = "{\"sessionId\": \"session-2\", \"machineId\": \"machine-2\", \"startAt\": 1688058278}";
		sqsService.publishMessage("events_queue", session1);
		sqsService.publishMessage("events_queue", session2);
		sleep(ReadMessagesTask.READ_MESSAGES_TASK_INITIAL_DELAY_MS + 2*ReadMessagesTask.READ_MESSAGES_TASK_INITIAL_DELAY_MS);
		String resultAsJson = apiGetRequest("/machines/list");
		assertEquals("[{\"machineId\":\"machine-1\"},{\"machineId\":\"machine-2\"}]", resultAsJson);
	}
}
