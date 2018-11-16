package org.abondar.experimental.mobilemessaging;

import org.abondar.experimental.mobilemessaging.model.TestModel;
import org.abondar.experimental.mobilemessaging.jms.JmsComponent;
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
        var msg = new TestModel(24L,"Alex");
        jmsComponent.sendTestMessage(msg);
        jmsComponent.sendTestMqttMessage(msg);
    }


    @Autowired
    public void setStompComponent(JmsComponent jmsComponent){
        Main.jmsComponent = jmsComponent;
    }


}
