package org.abondar.experimental.mobilemessaging.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.abondar.experimental.mobilemessaging.model.TestModel;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


@Component
public class JmsComponent {

    private ObjectMapper om = new ObjectMapper();

    private static final Logger logger = LogManager.getLogger();

    @JmsListener(destination = "${jsa.activemq.queue.stomp}")
    public void receiveMessage(final TextMessage msg) throws Exception {
        printMessage(msg);
    }

    @SendTo("${jsa.activemq.queue.stomp}")
    public String sendMessage(TestModel msg) throws Exception {
        var jsonMsg = om.writeValueAsString(msg);
        logger.info("Sending to broker: "+ jsonMsg);
        return jsonMsg;
    }


    private void printMessage(TextMessage msg) throws JMSException, IOException {
        String data;

            var tm = (TextMessage) msg;
            data = tm.getText();

            var test = om.readValue(data, TestModel.class);
            logger.info("Got message from broker: "+test);

    }
}