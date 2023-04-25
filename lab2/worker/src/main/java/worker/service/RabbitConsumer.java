package worker.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;

import java.io.IOException;

@Service
@Slf4j
@EnableRabbit
public class RabbitConsumer {
    private final CrackService crackService;

    private final RabbitProducer rabbitProducer;

    public RabbitConsumer(CrackService crackService, RabbitProducer rabbitProducer) {
        this.crackService = crackService;
        this.rabbitProducer = rabbitProducer;
    }

    @RabbitListener(queues = "${crackHashService.worker.queue.input}")
    public void receiveMessage(CrackHashManagerRequest message,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Received message: {}", message);
        crackService.putTask(message);
        rabbitProducer.cacheTag(channel, message.getRequestId(), message.getPartNumber(), tag);
    }
}
