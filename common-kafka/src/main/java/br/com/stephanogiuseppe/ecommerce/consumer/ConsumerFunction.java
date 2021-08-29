package br.com.stephanogiuseppe.ecommerce.consumer;

import br.com.stephanogiuseppe.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface  ConsumerFunction<T> {
    void consume(ConsumerRecord<String, Message<T>> record) throws Exception;
}
