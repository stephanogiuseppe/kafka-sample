package br.com.stephanogiuseppe.ecommerce;

import br.com.stephanogiuseppe.ecommerce.consumer.ConsumerService;
import br.com.stephanogiuseppe.ecommerce.consumer.ServiceRunner;
import br.com.stephanogiuseppe.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService implements ConsumerService<Order> {

    private final LocalDatabase database;

    FraudDetectorService() throws SQLException {
        this.database = new LocalDatabase("frauds_database");
        this.database.createIfNotExists(
        "create table Orders (" +
            "uuid varchar(200) primary key," +
            "is_fraud boolean)"
        );
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return FraudDetectorService.class.getSimpleName();
    }

    public static void main(String[] args) {
        new ServiceRunner<>(FraudDetectorService::new).start(1);
    }

    private final KafkaDispatcher<Order> orderDispatcher =
        new KafkaDispatcher<>();

    public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException, ExecutionException, InterruptedException {
        logExecution(record);

        var message = record.value();
        var order = message.getPayload();

        if (wasProcessed(order)) {
            System.out.println("Order " + order.getOrderId() + " was already processed");
            return;
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // ignoring
            e.printStackTrace();
        }

        if (isFraud(order)) {
            rejectOrder(order, message);
        } else {
            approveOrder(order, message);
        }

    }

    private boolean wasProcessed(Order order) throws SQLException {
        var results = database.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
        return results.next();
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

    private void logExecution(ConsumerRecord<String, Message<Order>> record) {
        System.out.println("Processing new order. Checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
    }

    private void approveOrder(Order order, Message<Order> message) throws ExecutionException, InterruptedException, SQLException {
        database.update("insert into Orders (uuid,is_fraud) values (?,false)", order.getOrderId());
        System.out.println("Approved: " + order);
        orderDispatcher.send(
                "ECOMMERCE_ORDER_APPROVED",
                order.getEmail(),
                message.getId().continueWith(FraudDetectorService.class.getSimpleName()),
                order
        );
    }

    private void rejectOrder(Order order, Message<Order> message) throws ExecutionException, InterruptedException, SQLException {
        database.update("insert into Orders (uuid,is_fraud) values (?,true)", order.getOrderId());

        System.out.println("Order is a fraud!" + order);
        orderDispatcher.send(
                "ECOMMERCE_ORDER_REJECTED",
                order.getEmail(),
                message.getId().continueWith(FraudDetectorService.class.getSimpleName()),
                order
        );
    }

}
