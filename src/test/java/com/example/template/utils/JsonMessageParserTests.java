package com.example.template.utils;

import com.example.template.models.Event;
import com.example.template.models.Session;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static com.example.template.utils.JsonMessageParser.parseJsonMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonMessageParserTests {

    @Test
    void when_parsing_incorrect_json_returns_empty_list() {
        String incorrectJson = "{" +
                "\"sessionId\": \"asfla-asdf-asdfa\", " +
                "\"machineId\" \"2343-asdf-fads\", " +
                "\"startAt\": 1688057249" +
                "}";
        List<Object> result =  parseJsonMessage(incorrectJson);
        assertEquals(0, result.size());
    }

    @Test
    void when_parsing_unknown_entity_returns_empty_list() {
        String unknownJson = "{" +
                "\"sessionId\": \"asfla-asdf-asdfa\", " +
                "\"unknownId\": \"2343-asdf-fads\", " +
                "\"startAt\": 1688057249" +
                "}";
        List<Object> result =  parseJsonMessage(unknownJson);
        assertEquals(0, result.size());
    }

    @Test
    void when_parsing_session_json_returns_list_of_one_session() {
        String correctSessionJson = "{" +
                "\"sessionId\": \"asfla-asdf-asdfa\", " +
                "\"machineId\": \"2343-asdf-fads\", " +
                "\"startAt\": 1688057249" +
                "}";
        List<Object> result =  parseJsonMessage(correctSessionJson);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof Session);
        assertEquals("asfla-asdf-asdfa", ((Session)result.get(0)).getSessionId());
        assertEquals("2343-asdf-fads", ((Session)result.get(0)).getMachineId());
        assertEquals(new Timestamp(1688057249000L), ((Session)result.get(0)).getStartAt());
    }

    @Test
    void when_parsing_event_json_returns_list_of_events() {
        String correctEventJson = "{" +
                "\"sessionId\": \"asfla-asdf-asdfa\", " +
                "\"events\": [" +
                "{\"eventAt\": 1688057249, \"eventType\": \"drivenDistance\", \"numericEventValue\": 123.34}," +
                "{\"eventAt\": 1688058249, \"eventType\": \"consumedFuel\", \"numericEventValue\": 2.7812}" +
                "]" +
                "}";
        List<Object> result =  parseJsonMessage(correctEventJson);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof Event);
        assertTrue(result.get(1) instanceof Event);
        assertEquals("asfla-asdf-asdfa", ((Event)result.get(0)).getSessionId());
        assertEquals("drivenDistance", ((Event)result.get(0)).getEventType());
        assertEquals(new Timestamp(1688057249000L), ((Event)result.get(0)).getEventAt());
        assertEquals(new BigDecimal("123.34"), ((Event)result.get(0)).getEventValue());
        assertEquals("asfla-asdf-asdfa", ((Event)result.get(1)).getSessionId());
        assertEquals("consumedFuel", ((Event)result.get(1)).getEventType());
        assertEquals(new Timestamp(1688058249000L), ((Event)result.get(1)).getEventAt());
        assertEquals(new BigDecimal("2.7812"), ((Event)result.get(1)).getEventValue());
    }
}
