package org.motechproject.ananya;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TestClass {

    public static void main(String[] args) throws JMSException, InterruptedException {

// Create the connection using Activemq implementations for simplicity (use JMS interfaces for real code)
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                null, null, "failover:(tcp://localhost:61616,tcp://localhost:61617)?randomize=false");
        Connection connection = connectionFactory.createConnection();
        connection.start();
// Create the Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
// And the Destination queue
        Destination destination = session.createQueue("single.queue");
// Create the producer
        MessageProducer producer = session.createProducer(destination);
        Message m = session.createTextMessage("hello");
//Send 3 messages at a time
        for (int i = 0; i < 100; i++) {
            Thread.sleep(2000L);
            System.out.println("Creating message " + i);
            producer.send(m);
        }
//shutdown with NO Error handling
        connection.close();
    }
}
