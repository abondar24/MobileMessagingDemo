package org.abondar.experimental.mobilemessaging.motioncapturer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.BytesMessage;
import javax.jms.JMSException;


@Component
public class JmsComponent {


    private static final Logger logger = LogManager.getLogger();

    @JmsListener(destination = "${jsa.activemq.topic.mqtt}")
    public void receiveMessage(final BytesMessage msg) throws Exception {
        printMessage(msg);
    }

    private void printMessage(BytesMessage msg) throws JMSException{
        byte[] data = new byte[(int) msg.getBodyLength()];
        msg.readBytes(data);

        String msgText = new String(data);

        logger.info("Got message from broker: " + msgText);

    }
}
