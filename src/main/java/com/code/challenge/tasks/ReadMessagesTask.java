package com.code.challenge.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.Message;
import com.code.challenge.models.*;
import com.code.challenge.services.SessionService;
import com.code.challenge.services.SqsService;
import com.code.challenge.services.EventService;
import com.code.challenge.utils.JsonMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReadMessagesTask {

    public static final int READ_MESSAGES_TASK_FIXED_DELAY_MS = 1000;
    public static final int READ_MESSAGES_TASK_INITIAL_DELAY_MS = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final SqsService sqsService;
    private final EventService eventService;
    private final SessionService sessionService;

    public ReadMessagesTask(AmazonSQS amazonSQS, SessionRepository sessionRepository, EventRepository eventRepository,
                            EventAggregationRepository eventAggregationRepository, MachineRepository machineRepository) {
        this.sqsService = new SqsService(amazonSQS);
        this.eventService = new EventService(eventRepository, eventAggregationRepository);
        this.sessionService = new SessionService(sessionRepository, eventRepository, machineRepository);
    }

    @Scheduled(initialDelay = READ_MESSAGES_TASK_INITIAL_DELAY_MS, fixedDelay = READ_MESSAGES_TASK_FIXED_DELAY_MS)
    public void readMessages() {
        LOGGER.info("Executing task ({})", dateFormat.format(new Date()));
        List<Message> messages =  sqsService.receiveMessages("events_queue");
        List<Object> parsedObjects = new ArrayList<>();
        messages.forEach(message -> {
            LOGGER.info("Message body {} ({})", message.getBody(), dateFormat.format(new Date()));
            parsedObjects.addAll(JsonMessageParser.parseJsonMessage(message.getBody()));
            DeleteMessageResult deleteResult = sqsService.deleteMessage("events_queue", message.getReceiptHandle());
            LOGGER.info("Deleted from queue {} ({})", deleteResult, dateFormat.format(new Date()));
        });
        parsedObjects.forEach(parsedObject -> {
            if (parsedObject instanceof Event) { eventService.processEvent((Event)parsedObject); }
            if (parsedObject instanceof Session) { sessionService.processSession((Session)parsedObject); }
        });
    }
}