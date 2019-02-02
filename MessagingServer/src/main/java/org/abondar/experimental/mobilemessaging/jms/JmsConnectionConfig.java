package org.abondar.experimental.mobilemessaging.jms;

import org.apache.activemq.*;
import org.springframework.context.annotation.*;
import org.springframework.jms.config.*;

import javax.jms.*;

@Configuration
public class JmsConnectionConfig {
    @Bean(name="stomp")
    public JmsListenerContainerFactory stomp(){
        var factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsConnectionFactory());
        return factory;
    }

    @Bean(name="mqtt")
    public JmsListenerContainerFactory mqtt(){
        var factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsConnectionFactory());
        factory.setPubSubDomain(true);
        return factory;
    }

    @Bean(name="mqttAlert")
    public JmsListenerContainerFactory mqttAlert(){
        var factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsConnectionFactory());
        factory.setPubSubDomain(true);
        return factory;
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }
}
