package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse.warehousemanagementsystem.exception.NotFoundException;
import warehouse.warehousemanagementsystem.order.Order;
import warehouse.warehousemanagementsystem.product.ProductInOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrdersHistoryService {
    private final OrdersHistoryDao ordersHistoryDao;

    @Autowired
    public OrdersHistoryService(OrdersHistoryDao ordersHistoryDao) {
        this.ordersHistoryDao = ordersHistoryDao;
    }

    public List<OrdersHistory> getAllOrders() {
        return ordersHistoryDao.getAllOrders();
    }

    @Transactional
    public Order getOrderById(Long orderId) {
        Order order = ordersHistoryDao.getOrderById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));

        List<ProductInOrder> products = ordersHistoryDao.getProductsInOrder(order.id());

        BigDecimal totalPrice = new BigDecimal(0);
        for (ProductInOrder product : products) {
            totalPrice = totalPrice.add(product.price());
        }
        return new Order(order.id(), order.customer(), order.dateProcessed(), order.worker(), order.status(), order.dateReceived(), totalPrice, products);
    }

    public List<OrdersHistory> getOrdersByWorker(Long workerId) {
        return ordersHistoryDao.getOrdersByWorker(workerId);
    }

    public List<OrdersHistory> getOrderByWorkerWithDates(Long workerId, Date processedDateMin, Date processedDateMax) {
        return ordersHistoryDao.getOrderByWorkerWithDates(workerId, processedDateMin, processedDateMax);
    }

    public List<OrdersHistory> getOrdersByCustomer(String email) {
        return ordersHistoryDao.getOrdersByCustomer(email);
    }

    public List<OrdersHistoryView> getAllOrdersViews() {
        return ordersHistoryDao.getAllOrdersViews();
    }

    public List<OrdersHistoryView> getOrdersViewByUsernameSubstr (String usernameSubstr) {
        return ordersHistoryDao.getOrdersHistViewsByWorkerUsernameSubstring(usernameSubstr);
    }

    public List<OrdersHistoryView> getOrdersViewByEmail (String email) {
        return ordersHistoryDao.getOrdersHistViewsByCustomerEmailSubstring(email);
    }

    public List<OrdersHistoryView> getOrdersViewByEmailAndUsername(String email, String username) {
        return ordersHistoryDao.getOrdersHistViewsByCustomerEmailWorkerUsernameSubstring(email, username);
    }

    public List<OrdersHistoryView> getOrdersViewByOrderId(Long orderId) {
        return ordersHistoryDao.getOrdersHistViewsByOrderId(orderId);
    }

    @Transactional
    public List<Order> getOrders(String customerEmail, String workerUsername) {
        List<Order> orders = ordersHistoryDao.getOrders(customerEmail, workerUsername);

        List<Order> completedOrders = new ArrayList<>();
        for (Order order : orders) {
            List<ProductInOrder> products = ordersHistoryDao.getProductsInOrder(order.id());
            BigDecimal totalPrice = new BigDecimal(0);
            for (ProductInOrder product : products) {
                totalPrice = totalPrice.add(product.price());
            }
            order = new Order(order.id(), order.customer(), order.dateProcessed(), order.worker(), order.status(), order.dateReceived(), totalPrice, products);
            completedOrders.add(order);
        }
        return completedOrders;
    }
}
