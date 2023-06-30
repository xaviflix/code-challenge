package com.example.template.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.Message;
import com.example.template.models.*;
import com.example.template.services.EventService;
import com.example.template.services.SessionService;
import com.example.template.services.SqsService;
import com.example.template.utils.JsonMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReadMessagesTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final SqsService sqsService;
    private final EventService eventService;
    private final SessionService sessionService;

    public ReadMessagesTask(AmazonSQS amazonSQS, SessionRepository sessionRepository, EventRepository eventRepository,
                            EventAggregationRepository eventAggregationRepository) {
        this.sqsService = new SqsService(amazonSQS);
        this.eventService = new EventService(eventRepository, eventAggregationRepository);
        this.sessionService = new SessionService(sessionRepository, eventRepository);
    }

    @Scheduled(initialDelay = 1000, fixedRate = 1000)
    public void readMessages() {
        LOGGER.info("Executing task ({})", dateFormat.format(new Date()));
        List<Message> messages =  sqsService.receiveMessages("events_queue");
        List<Object> parsedObjects = new ArrayList<Object>();
        messages.forEach(message -> {
            LOGGER.info("Message body {} ({})", message.getBody(), dateFormat.format(new Date()));
            parsedObjects.addAll(JsonMessageParser.parseJsonMessage(message.getBody()));
            DeleteMessageResult deleteResult = sqsService.deleteMessage("events_queue", message.getReceiptHandle());
            LOGGER.info("Deleted from queue {} ({})", deleteResult, dateFormat.format(new Date()));
        });
        parsedObjects.forEach(parsedObject -> {
            if (parsedObject instanceof Event) { eventService.processEvent((Event)parsedObject); };
            if (parsedObject instanceof Event) { sessionService.processSession((Session)parsedObject); };
        });
    }
}