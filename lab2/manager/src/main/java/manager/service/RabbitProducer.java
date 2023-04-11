package manager.service;

import lombok.extern.slf4j.Slf4j;
import manager.model.entity.Request;
import manager.model.repository.RequestsRepository;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;

import java.util.List;

@Service
@Slf4j
public class RabbitProducer implements ConnectionListener {
    private final AmqpTemplate amqpTemplate;

    @Value("${crackHashService.manager.queue.output}")
    String outputQueue;

    @Autowired
    private RequestsRepository crackTaskRequestRepository;

    public RabbitProducer(AmqpTemplate amqpTemplate, ConnectionFactory connectionFactory) {
        this.amqpTemplate = amqpTemplate;
        connectionFactory.addConnectionListener(this);
    }

    public boolean trySendMessage(CrackHashManagerRequest request) {
        try {
            amqpTemplate.convertAndSend(outputQueue, request);
            log.info("Set {} part of {} task request was sent", request.getPartNumber(), request.getRequestId());
            return true;
        } catch (AmqpException ex) {
            log.error("Failed to send request '{}', cached message", request.getRequestId());
            return false;
        }
    }

    @Override
    public void onCreate(Connection connection) {
        List<Request> requests = crackTaskRequestRepository.findAll();
        for (var request : requests) {
            CrackHashManagerRequest message = request.getRequest();
            try {
                amqpTemplate.convertAndSend(outputQueue, message);
                crackTaskRequestRepository.delete(request);
                log.info("Set {} part of {} task request was sent", message.getPartNumber(), message.getRequestId());
            } catch (Exception ex) {
                log.error("Failed to resend request '{}'", message.getRequestId());
            }
        }
    }
}
