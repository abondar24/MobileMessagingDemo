package org.abondar.experimental.mobilemessaging.motioncapturer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class Main {


    private static JmsComponent jmsComponent;


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class);
    }

}
