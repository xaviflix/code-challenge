package com.code.challenge.controllers;

import com.code.challenge.models.EventAggregationRepository;
import com.code.challenge.models.EventRepository;
import com.code.challenge.models.MachineRepository;
import com.code.challenge.models.SessionRepository;
import com.code.challenge.services.SqsService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTests {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected SqsService sqsService;

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private MachineRepository machineRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventAggregationRepository eventAggregationRepository;

    @BeforeEach
    protected void setup() {
        sessionRepository.deleteAll();
        machineRepository.deleteAll();
        eventRepository.deleteAll();
        eventAggregationRepository.deleteAll();
    }

    protected String apiGetRequest(String url_with_params) {
        return restTemplate.getForObject(url_with_params, String.class);
    }

}
