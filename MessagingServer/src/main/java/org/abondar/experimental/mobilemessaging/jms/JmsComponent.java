package org.abondar.experimental.mobilemessaging.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.abondar.experimental.mobilemessaging.model.LocationData;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;

import org.springframework.jms.config.*;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import javax.jms.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


@Component
public class JmsComponent {

    private ObjectMapper om = new ObjectMapper();

    private static final Logger logger = LogManager.getLogger();

    @JmsListener(id="stompListener",destination = "${jsa.activemq.topic.stomp}",containerFactory="stomp")
    public void receiveMessage(final Message msg) throws Exception {
            printMessage(msg);
    }


    @JmsListener(id="mqttAlertListener",destination = "${jsa.activemq.topic.mqtt.alert}", containerFactory="mqttAlert")
    public void receiveAlertMessage(final Message msg) throws Exception {
        printMessage(msg);
    }

    @JmsListener(id="mqttListener",destination = "${jsa.activemq.topic.mqtt}", containerFactory="mqtt")
    public void receiveMqttMessage(final Message msg) throws Exception {
        printMessage(msg);
    }

    @SendTo({"${jsa.activemq.topic.stomp}"})
    public void sendMessage(LocationData msg) throws Exception {
        var jsonMsg = om.writeValueAsString(msg);
        logger.info("Sending to broker: " + jsonMsg);
    }

    private void printMessage(Message msg) throws JMSException, IOException{
        String msgText="";
        if (msg instanceof TextMessage){
            TextMessage tmsg = (TextMessage) msg;
            var data = tmsg.getText();
            msgText =  om.readValue(data, LocationData.class).toString();

        } else if (msg instanceof BytesMessage) {
            BytesMessage bmsg = (BytesMessage) msg;

            byte[] data = new byte[(int) bmsg.getBodyLength()];
            bmsg.readBytes(data);

            msgText = new String(data);
        }

        logger.info("Got message from broker: " + msgText);
    }



}
