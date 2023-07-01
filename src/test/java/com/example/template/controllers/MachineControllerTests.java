package com.example.template.controllers;

import com.amazonaws.services.sqs.model.SendMessageResult;
import com.example.template.models.MachineRepository;
import com.example.template.models.SessionRepository;
import com.example.template.services.SessionService;
import com.example.template.services.SqsService;
import com.example.template.tasks.ReadMessagesTask;
import com.example.template.tasks.ReadMessagesTaskTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MachineControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SqsService sqsService;

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private MachineRepository machineRepository;

	@BeforeEach
	public void setup() {
		sessionRepository.deleteAll();
		machineRepository.deleteAll();
	}

	@Test
	void when_no_machine_exist_returns_empty_list() {
		String resultAsJson = restTemplate.getForObject("/machines/list", String.class);
		assertEquals("[]", resultAsJson);
	}

	@Test
	void when_several_machines_exist_returns_machines_list() throws Exception{
		String session1 = "{\"sessionId\": \"session-1\", \"machineId\": \"machine-1\", \"startAt\": 1688057249}";
		String session2 = "{\"sessionId\": \"session-2\", \"machineId\": \"machine-2\", \"startAt\": 1688058278}";
		sqsService.publishMessage("events_queue", session1);
		sqsService.publishMessage("events_queue", session2);
		sleep(ReadMessagesTask.READ_MESSAGES_TASK_INITIAL_DELAY_MS + 2*ReadMessagesTask.READ_MESSAGES_TASK_INITIAL_DELAY_MS);
		String resultAsJson = restTemplate.getForObject("/machines/list", String.class);
		assertEquals("[{\"machineId\":\"machine-1\"},{\"machineId\":\"machine-2\"}]", resultAsJson);
	}
}
