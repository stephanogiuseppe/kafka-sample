package br.com.stephanogiuseppe.ecommerce;

public class Order {

    private final String orderId;
    private final String value;
    private final String email;

    public Order(String orderId, String value, String email) {
        this.orderId = orderId;
        this.value = value;
        this.email = email;
    }
}