package br.com.stephanogiuseppe.ecommerce.consumer;

public interface ServiceFactory<T> {

    ConsumerService<T> create() throws Exception;
}
