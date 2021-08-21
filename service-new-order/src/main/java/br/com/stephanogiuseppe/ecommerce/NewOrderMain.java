package br.com.stephanogiuseppe.ecommerce;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                var email = Math.random() + "@email.com";
                for (var i = 0; i < 10; i++) {
                    var orderId = new CorrelationId(email);
                    var amount = new BigDecimal(Math.random() * 5000 + 1);

                    var order = new Order(orderId, amount, email);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, orderId, order);

                    var emailCode = "Thank you for your order! We are processing your order!";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, orderId, emailCode);
                }
            }
        }
    }
}
