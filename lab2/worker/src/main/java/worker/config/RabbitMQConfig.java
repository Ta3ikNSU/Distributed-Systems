package worker.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2XmlMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${crackHashService.worker.queue.input}")
    private String inputQueue;

    @Value("${crackHashService.worker.queue.output}")
    private String outputQueue;

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(xmlMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue workersToManagerQueue() {
        return new Queue(inputQueue, true);
    }

    @Bean
    public Queue managerToWorkersQueue() {
        return new Queue(outputQueue, true);
    }

    @Bean
    public Jackson2XmlMessageConverter xmlMessageConverter() {
        return new Jackson2XmlMessageConverter();
    }
}