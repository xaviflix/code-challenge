package com.example.template.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.example.template.models.User;
import com.example.template.models.UserRepository;
import com.example.template.services.SqsService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReadEventsTask {

    private static final Logger log = LoggerFactory.getLogger(ReadEventsTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private int counter = 0;

    @Autowired
    private SqsService sqsService;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(initialDelay = 1000, fixedRate = 1000)
    public void readEvents() {
        JSONObject json = new JSONObject();
        json.put("iteration", counter);

        SendMessageResult sendResult = sqsService.publishMessage("events_queue", json.toString());
        log.info("Write in queue {} ({})", sendResult, dateFormat.format(new Date()));
        List<Message> messages =  sqsService.receiveMessages("events_queue");
        log.info("Read from queue {} ({})", messages.size(), messages, dateFormat.format(new Date()));
        messages.forEach(message -> {
            log.info("Message body {} ({})", message.getBody(), dateFormat.format(new Date()));
            DeleteMessageResult deleteResult = sqsService.deleteMessage("events_queue", message.getReceiptHandle());
            log.info("Deleted from queue {} ({})", deleteResult, dateFormat.format(new Date()));
        });

        User dbUser = new User();
        dbUser.setName(String.format("Name-%s", counter));
        dbUser.setEmail(String.format("email+%s@test.com", counter));
        userRepository.save(dbUser);

        counter ++;
    }
}