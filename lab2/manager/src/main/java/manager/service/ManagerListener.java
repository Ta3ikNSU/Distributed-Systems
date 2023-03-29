package manager.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class ManagerListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        System.out.println("Received message: " + message);
    }
}
