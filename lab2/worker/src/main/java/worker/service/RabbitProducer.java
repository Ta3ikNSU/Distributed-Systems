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

    private Channel channel;

    private final Map<String, Long> requestToTag = new HashMap<>();

    BiFunction<String, Integer, String> makeKey = (a, b) -> a.concat("_").concat(String.valueOf(b));

    public RabbitProducer(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void produce(CrackHashWorkerResponse response) {
        try {
//            makeAck(response.getRequestId(), response.getPartNumber());
            amqpTemplate.convertAndSend(outputQueue, response);
            log.info("Set {} part of {} task request was sent", response.getPartNumber(), response.getRequestId());
        } catch (AmqpException ex) {
            log.error("Failed to send request '{}', cached message", response.getRequestId());
        }
    }
    private void makeAck(String requestId, int partNumber) {
        String key = makeKey.apply(requestId, partNumber);
        Long tag = requestToTag.get(key);
        try {
            channel.basicAck(tag, false);
            log.info("maka requestId : {}, ask {}", requestId, tag);
            requestToTag.remove(key, tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cacheTag(Channel channel, String requestId, Integer partNumber, long tag) {
        this.channel = channel;
        requestToTag.put(makeKey.apply(requestId, partNumber), tag);
    }
}
