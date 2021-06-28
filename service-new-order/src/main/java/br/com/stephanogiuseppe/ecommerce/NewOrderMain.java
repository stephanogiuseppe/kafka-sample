package br.com.stephanogiuseppe.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                var email = Math.random() + "@email.com";
                for (var i = 0; i < 10; i++) {
                    var order = createOrder(email);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

                    var emailBody = "Thank you for your order! We are processing your order!";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailBody);
                }
            }
        }
    }

    private static Order createOrder(String email) {
        var orderId = UUID.randomUUID().toString();
        var amount = new BigDecimal(Math.random() * 5000 + 1);
        return new Order(orderId, amount, email);
    }
}
