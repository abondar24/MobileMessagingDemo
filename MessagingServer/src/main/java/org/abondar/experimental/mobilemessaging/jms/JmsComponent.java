package org.abondar.experimental.mobilemessaging.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.abondar.experimental.mobilemessaging.model.LocationData;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


@Component
public class JmsComponent {

    private ObjectMapper om = new ObjectMapper();

    private static final Logger logger = LogManager.getLogger();

    @JmsListener(destination = "${jsa.activemq.topic.stomp}")
    public void receiveMessage(final TextMessage msg) throws Exception {
        printMessage(msg);
    }

    @JmsListener(destination = "${jsa.activemq.topic.mqtt}")
    public void receiveMessage(final BytesMessage msg) throws Exception {
        printMessage(msg);
    }

    @JmsListener(destination = "${jsa.activemq.topic.mqtt.alert}")
    public void receiveAlertMessage(final BytesMessage msg) throws Exception {
        printMessage(msg);
    }


    @SendTo("${jsa.activemq.topic.stomp}")
    public String sendMessage(LocationData msg) throws Exception {
        var jsonMsg = om.writeValueAsString(msg);
        logger.info("Sending to broker: " + jsonMsg);
        return jsonMsg;
    }


    private void printMessage(BytesMessage msg) throws JMSException{
        byte[] data = new byte[(int) msg.getBodyLength()];
        msg.readBytes(data);

        String msgText = new String(data);

        logger.info("Got message from broker: " + msgText);

    }

    private void printMessage(TextMessage msg) throws JMSException, IOException {
        String data;

        data = msg.getText();

        var test = om.readValue(data, LocationData.class);
        logger.info("Got message from broker: " + test);

    }
}
