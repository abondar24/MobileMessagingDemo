package org.abondar.experimental.mobilemessaging;

import org.abondar.experimental.mobilemessaging.jms.JmsComponent;
import org.abondar.experimental.mobilemessaging.model.LocationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class Main {


    private static JmsComponent jmsComponent;


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class);
        sendTest();
    }

    private static void sendTest()throws Exception {
        var testData = new LocationData("55.1111","56.11111","57.888");
        testData.setDeviceId("ffff-fffff");
        jmsComponent.sendMessage(testData);
    }


    @Autowired
    public void setJmsComponent(JmsComponent jmsComponent){
        Main.jmsComponent = jmsComponent;
    }


}
