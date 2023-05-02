package worker.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics.IntegerGenerator;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Service
@Slf4j
public class RabbitProducer {

    @Value("${crackHashService.worker.queue.output}")
    String outputQueue;

    private final AmqpTemplate amqpTemplate;

    public RabbitProducer(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void produce(CrackHashWorkerResponse response) {
        try {
            amqpTemplate.convertAndSend(outputQueue, response);
            log.info("Set {} part of {} task request was sent", response.getPartNumber(), response.getRequestId());
        } catch (AmqpException ex) {
            log.error("Failed to send request '{}', cached message", response.getRequestId());
        }
    }
}
