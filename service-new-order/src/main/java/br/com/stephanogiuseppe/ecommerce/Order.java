package br.com.stephanogiuseppe.ecommerce;

import java.math.BigDecimal;

public class Order {

    private final String orderId;
    private final BigDecimal value;
    private final String email;

    public Order(CorrelationId correlationId, BigDecimal value, String email) {
        this.orderId = correlationId.toString();
        this.value = value;
        this.email = email;
    }
}
