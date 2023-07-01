package com.code.challenge.tasks;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.code.challenge.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReadMessagesTaskTests {

    private ReadMessagesTask readMessagesTask;

    private AmazonSQS amazonSQS = Mockito.mock(AmazonSQS.class);
    private SessionRepository sessionRepository = Mockito.mock(SessionRepository.class);
    private EventRepository eventRepository = Mockito.mock(EventRepository.class);
    private MachineRepository machineRepository = Mockito.mock(MachineRepository.class);
    private EventAggregationRepository eventAggregationRepository = Mockito.mock(EventAggregationRepository.class);

    @BeforeEach
    void before() {
        readMessagesTask = new ReadMessagesTask(
                amazonSQS, sessionRepository, eventRepository, eventAggregationRepository, machineRepository);
    }

    @Test
    void when_no_new_messages_do_nothing() {
        // Mock
        GetQueueUrlResult getQueueUrlResult = new GetQueueUrlResult();
        getQueueUrlResult.setQueueUrl("mocked-queue-url");
        ReceiveMessageResult sqsReceiveMessageResult = new ReceiveMessageResult();
        when(amazonSQS.getQueueUrl(any(String.class))).thenReturn(getQueueUrlResult);
        when(amazonSQS.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(sqsReceiveMessageResult);

        // When
        readMessagesTask.readMessages();

        // Then
        verify(amazonSQS, never()).deleteMessage(any(DeleteMessageRequest.class));
        verify(sessionRepository, never()).save(any(Session.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void when_new_single_session_message_process_session() {
        // Mock
        GetQueueUrlResult getQueueUrlResult = new GetQueueUrlResult();
        getQueueUrlResult.setQueueUrl("mocked-queue-url");
        ReceiveMessageResult sqsReceiveMessageResult = new ReceiveMessageResult();
        Message sqsMessage = new Message();
        sqsMessage.setReceiptHandle("sqs-msg-receipt-handler");
        String correctSessionJson = "{" +
                "\"sessionId\": \"asfla-asdf-asdfa\", " +
                "\"machineId\": \"2343-asdf-fads\", " +
                "\"startAt\": 1688057249" +
                "}";
        sqsMessage.setBody(correctSessionJson);
        List<Message> receivedMessages = List.of(sqsMessage);
        sqsReceiveMessageResult.setMessages(receivedMessages);
        when(amazonSQS.getQueueUrl(any(String.class))).thenReturn(getQueueUrlResult);
        when(amazonSQS.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(sqsReceiveMessageResult);

        // When
        readMessagesTask.readMessages();

        // Then
        verify(amazonSQS, times(1)).deleteMessage(any(DeleteMessageRequest.class));
        verify(sessionRepository, times(1)).save(any(Session.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void when_new_single_events_message_process_events() {
        // Mock
        GetQueueUrlResult getQueueUrlResult = new GetQueueUrlResult();
        getQueueUrlResult.setQueueUrl("mocked-queue-url");
        ReceiveMessageResult sqsReceiveMessageResult = new ReceiveMessageResult();
        Message sqsMessage = new Message();
        sqsMessage.setReceiptHandle("sqs-msg-receipt-handler");
        String correctEventJson = "{" +
                "\"sessionId\": \"asfla-asdf-asdfa\", " +
                "\"events\": [" +
                "{\"eventAt\": 1688057249, \"eventType\": \"drivenDistance\", \"numericEventValue\": 123.34}," +
                "{\"eventAt\": 1688058249, \"eventType\": \"consumedFuel\", \"numericEventValue\": 2.7812}" +
                "]" +
                "}";
        sqsMessage.setBody(correctEventJson);
        List<Message> receivedMessages = List.of(sqsMessage);
        sqsReceiveMessageResult.setMessages(receivedMessages);
        when(amazonSQS.getQueueUrl(any(String.class))).thenReturn(getQueueUrlResult);
        when(amazonSQS.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(sqsReceiveMessageResult);

        // When
        readMessagesTask.readMessages();

        // Then
        verify(amazonSQS, times(1)).deleteMessage(any(DeleteMessageRequest.class));
        verify(sessionRepository, never()).save(any(Session.class));
        verify(eventRepository, times(2)).save(any(Event.class));
    }

    @Test
    void when_multiple_session_and_event_messages_process_sessions_and_events() {
        // Mock
        GetQueueUrlResult getQueueUrlResult = new GetQueueUrlResult();
        getQueueUrlResult.setQueueUrl("mocked-queue-url");
        ReceiveMessageResult sqsReceiveMessageResult = new ReceiveMessageResult();
        Message sqsEventMessage = new Message();
        sqsEventMessage.setReceiptHandle("sqs-msg-receipt-handler-event");
        String correctEventJson = "{" +
                "\"sessionId\": \"asfla-asdf-asdfa\", " +
                "\"events\": [" +
                "{\"eventAt\": 1688057249, \"eventType\": \"drivenDistance\", \"numericEventValue\": 123.34}," +
                "{\"eventAt\": 1688058249, \"eventType\": \"consumedFuel\", \"numericEventValue\": 2.7812}" +
                "]" +
                "}";
        sqsEventMessage.setBody(correctEventJson);
        Message sqsSessionMessage = new Message();
        sqsSessionMessage.setReceiptHandle("sqs-msg-receipt-handler-session");
        String correctSessionJson = "{" +
                "\"sessionId\": \"asfla-asdf-asdfa\", " +
                "\"machineId\": \"2343-asdf-fads\", " +
                "\"startAt\": 1688057249" +
                "}";
        sqsSessionMessage.setBody(correctSessionJson);
        List<Message> receivedMessages = List.of(sqsEventMessage, sqsSessionMessage);
        sqsReceiveMessageResult.setMessages(receivedMessages);
        when(amazonSQS.getQueueUrl(any(String.class))).thenReturn(getQueueUrlResult);
        when(amazonSQS.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(sqsReceiveMessageResult);

        // When
        readMessagesTask.readMessages();

        // Then
        verify(amazonSQS, times(2)).deleteMessage(any(DeleteMessageRequest.class));
        verify(sessionRepository, times(1)).save(any(Session.class));
        verify(eventRepository, times(2)).save(any(Event.class));
    }
}
