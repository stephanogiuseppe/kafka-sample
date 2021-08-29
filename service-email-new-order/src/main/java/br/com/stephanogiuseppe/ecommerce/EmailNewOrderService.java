package br.com.stephanogiuseppe.ecommerce;

import br.com.stephanogiuseppe.ecommerce.consumer.ConsumerService;
import br.com.stephanogiuseppe.ecommerce.consumer.ServiceRunner;
import br.com.stephanogiuseppe.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {

    public static void main(String[] args) {
        new ServiceRunner<>(EmailNewOrderService::new).start(1);
    }

    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("Processing new order and preparing email");
        var message = record.value();
        System.out.println(message);

        var order = message.getPayload();
        var emailCode = "We are processing your order!";
        var id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        emailDispatcher.send(
           "ECOMMERCE_SEND_EMAIL",
            order.getEmail(),
            id,
            emailCode
        );

    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }

}
