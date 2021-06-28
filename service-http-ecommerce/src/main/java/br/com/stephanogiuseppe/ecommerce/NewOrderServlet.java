package br.com.stephanogiuseppe.ecommerce;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class   NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderKafkaDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String> emKafkaDispatcher = new KafkaDispatcher<>();

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // @TODO: security issues
            var email = req.getParameter("email");
            var order = createOrder(email, req.getParameter("amount"));
            orderKafkaDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

            var emailBody = "Thank you for your order! We are processing your order!";
            emKafkaDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailBody);

            System.out.println("New Order sent successfully.");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New Order sent");
        } catch (ExecutionException | InterruptedException e) {
            throw new ServletException(e);
        }
    }

    private static Order createOrder(String email, String amount) {
        var orderId = UUID.randomUUID().toString();
        return new Order(orderId, amount, email);
    }
}
