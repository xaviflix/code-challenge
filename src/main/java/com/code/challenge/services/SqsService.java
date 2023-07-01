package com.code.challenge.services;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqsService {

    public static final int SQS_MAX_WAIT_SECONDS = 5;
    public static final int SQS_MAX_MESSAGES_TO_READ = 10;
    private static final Logger LOGGER = LoggerFactory.getLogger(SqsService.class);
    private final AmazonSQS amazonSQS;

    public SqsService(AmazonSQS amazonSQS) {
        this.amazonSQS = amazonSQS;
    }

    public SendMessageResult publishMessage(final String queueName, final String message) {
        try {
            GetQueueUrlResult queueUrlResult = amazonSQS.getQueueUrl(queueName);
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(queueUrlResult.getQueueUrl())
                    .withMessageBody(message);
            return amazonSQS.sendMessage(sendMessageRequest);
        } catch (Exception e) {
            LOGGER.error("Exception e : {}", e.getMessage());
        }
        return null;
    }

    public List<Message> receiveMessages(final String queueName) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
        GetQueueUrlResult queueUrlResult = amazonSQS.getQueueUrl(queueName);
        receiveMessageRequest.setQueueUrl(queueUrlResult.getQueueUrl());
        receiveMessageRequest.setWaitTimeSeconds(SQS_MAX_WAIT_SECONDS);
        receiveMessageRequest.setMaxNumberOfMessages(SQS_MAX_MESSAGES_TO_READ);
        return amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
    }

    public DeleteMessageResult deleteMessage(final String queueName, final String receiptHandle) {
        try {
            GetQueueUrlResult queueUrlResult = amazonSQS.getQueueUrl(queueName);
            DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest()
                    .withQueueUrl(queueUrlResult.getQueueUrl())
                    .withReceiptHandle(receiptHandle);
            return amazonSQS.deleteMessage(deleteMessageRequest);
        } catch (Exception e) {
            LOGGER.error("Exception e : {}", e.getMessage());
        }
        return null;
    }

}
