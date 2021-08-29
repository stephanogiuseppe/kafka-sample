package br.com.stephanogiuseppe.ecommerce.consumer;

import br.com.stephanogiuseppe.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService<T> {

    void parse(ConsumerRecord<String, Message<T>> record) throws Exception;

    String getTopic();

    String getConsumerGroup();
}
