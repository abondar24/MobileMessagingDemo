package org.abondar.experimental.locationtracker.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.abondar.experimental.locationtracker.data.LocationData;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TrackerTest {

    private ObjectMapper mapper;

    @Before
    public void setUp(){
        mapper = new ObjectMapper();
    }


    @Test
    public void locationJsonTest() throws Exception {
        LocationData testData = new LocationData("55.1111","56.11111","57.888");
        testData.setDeviceId("ffff-fffff");

        JsonNode json = mapper.valueToTree(testData);
        String jsonStr = mapper.writeValueAsString(testData);

        assertEquals(testData.getDeviceId(),json.get("id").asText());
        assertEquals(testData.getLatitude(),json.get("lat").asText());
        assertEquals(testData.getLongitude(),json.get("lon").asText());
        assertEquals(testData.getAltitude(),json.get("alt").asText());
        assertEquals('{',jsonStr.toCharArray()[0]);
    }
}
