package com.example.template.utils;

import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.example.template.models.Event;
import com.example.template.models.Session;
import com.example.template.models.User;
import com.example.template.models.UserRepository;
import com.example.template.services.SqsService;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JsonMessageParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMessageParser.class);

    private static final int UNKNOWN_OBJECT = -1;
    private static final int SESSION_OBJECT = 0;
    private static final int EVENT_OBJECT = 1;

    public static List<Object> parseJsonMessage(String jsonMessage) {
        try {
            JSONObject jsonObject = new JSONObject(jsonMessage);
            switch (_objectType(jsonObject)) {
                case SESSION_OBJECT -> { return _parseSession(jsonObject); }
                case EVENT_OBJECT -> {return _parseEvent(jsonObject); }
                default -> { return new ArrayList<Object>(); }
            }
        } catch (Exception e) {
            LOGGER.error("Exception e : {}", e.getMessage());
            return new ArrayList<Object>();
        }
    }

    private static int _objectType(JSONObject jsonObject) {
        boolean hasSessionId = jsonObject.has("sessionId");
        boolean hasMachineId = jsonObject.has("machineId");
        boolean hasEvents = jsonObject.has("events");
        if (hasSessionId && hasMachineId) {
            return SESSION_OBJECT;
        } else if (hasSessionId && hasEvents) {
            return EVENT_OBJECT;
        } else {
            return UNKNOWN_OBJECT;
        }
    }

    private static List<Object> _parseSession(JSONObject jsonObject) {
        List<Object> sessionList = new ArrayList<Object>();
        sessionList.add(
                Session.create(
                        jsonObject.optString("sessionId"),
                        jsonObject.optString("machineId"),
                        new Timestamp(jsonObject.optInt("startAt"))
                )
        );
        return sessionList;
    }

    private static List<Object> _parseEvent(JSONObject jsonObject) {
        List<Object> eventList = new ArrayList<Object>();
        String sessionId = jsonObject.optString("sessionId");
        JSONArray jsonEventsList = jsonObject.getJSONArray("events");
        jsonEventsList.forEach((jsonEvent) -> {
            eventList.add(
                    Event.create(
                            sessionId,
                            ((JSONObject)jsonEvent).optString("eventType"),
                            ((JSONObject)jsonEvent).optBigDecimal("numericEventValue", new BigDecimal(0.0)),
                            new Timestamp((((JSONObject)jsonEvent).optInt("eventAt")))
                    )
            );
        });
        return eventList;
    }
}