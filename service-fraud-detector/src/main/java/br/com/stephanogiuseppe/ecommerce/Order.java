package br.com.stephanogiuseppe.ecommerce;

import java.math.BigDecimal;

public class Order {

    private final String orderId;
    private final BigDecimal value;
    private String email;

    public Order(String orderId, BigDecimal value) {
        this.orderId = orderId;
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", value=" + value +
                ", email='" + email + '\'' +
                '}';
    }
}
