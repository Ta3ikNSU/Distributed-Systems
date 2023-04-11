package manager.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

import java.io.IOException;

@Service
@Slf4j
@EnableRabbit
public class RabbitConsumer {
    @Autowired
    CrackHashService crackHashService;

    @RabbitListener(queues = "${crackHashService.manager.queue.input}")
    public void receiveMessage(CrackHashWorkerResponse message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Received message: {}", message);
        crackHashService.handleWorkerCallback(message);
        channel.basicAck(tag, false);
    }
}
