package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse.warehousemanagementsystem.exception.NotFoundException;
import warehouse.warehousemanagementsystem.order.OrderDto;
import warehouse.warehousemanagementsystem.product.ProductInOrderDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersHistoryService {
    private final OrdersHistoryDao ordersHistoryDao;

    @Autowired
    public OrdersHistoryService(OrdersHistoryDao ordersHistoryDao) {
        this.ordersHistoryDao = ordersHistoryDao;
    }

    @Transactional
    public OrderDto getOrderById(Long orderId) {
        OrderDto order = ordersHistoryDao.getOrderById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));

        List<ProductInOrderDto> products = ordersHistoryDao.getProductsInOrder(order.id());

        BigDecimal totalPrice = new BigDecimal(0);
        for (ProductInOrderDto productInOrder : products) {
            BigDecimal itemPrice = productInOrder.price();
            BigDecimal amount = BigDecimal.valueOf(productInOrder.amount());
            totalPrice = totalPrice.add(itemPrice.multiply(amount));
        }
        return new OrderDto(order.id(), order.customer(), order.dateProcessed(), order.worker(), order.status(), order.dateReceived(), totalPrice, products);
    }

    public List<OrderDto> getOrders(String customerEmail, String workerUsername) {
        List<OrderDto> orders = ordersHistoryDao.getOrders(customerEmail, workerUsername);

        List<OrderDto> completedOrders = new ArrayList<>();
        for (OrderDto order : orders) {
            List<ProductInOrderDto> products = ordersHistoryDao.getProductsInOrder(order.id());
            BigDecimal totalPrice = new BigDecimal(0);
            for (ProductInOrderDto productInOrder : products) {
                BigDecimal itemPrice = productInOrder.price();
                BigDecimal amount = BigDecimal.valueOf(productInOrder.amount());
                totalPrice = totalPrice.add(itemPrice.multiply(amount));
            }
            order = new OrderDto(order.id(), order.customer(), order.dateProcessed(), order.worker(), order.status(), order.dateReceived(), totalPrice, products);
            completedOrders.add(order);
        }
        return completedOrders;
    }
}
