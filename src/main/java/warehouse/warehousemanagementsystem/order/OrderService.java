package warehouse.warehousemanagementsystem.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.NotFoundException;
import warehouse.warehousemanagementsystem.product.ProductDao;
import warehouse.warehousemanagementsystem.product.ProductInOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderDao orderDao;
    private final ProductDao productDao;

    @Autowired
    public OrderService(OrderDao orderDao, ProductDao productDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
    }

    @Transactional
    public List<Order> getOrders(String workerUsername, String customerEmail, String status, Long workerId) {
        List<Order> orders = orderDao.getOrders(workerUsername, customerEmail, status, workerId);

        List<Order> completedOrders = new ArrayList<>();
        for (Order order : orders) {
            List<ProductInOrder> productsInOrder = productDao.getProductsInOrder(order.id());
            BigDecimal totalPrice = new BigDecimal(0);
            for (ProductInOrder productInOrder : productsInOrder) {
                totalPrice = totalPrice.add(productInOrder.price());
            }
            order = new Order(order.id(), order.customer(), order.dateProcessed(), order.worker(), order.status(), order.dateReceived(), totalPrice, productsInOrder);
            completedOrders.add(order);
        }
        return completedOrders;
    }

    @Transactional
    public void packOrder(Order order) {
        order = orderDao.getOrderById(order.id()).orElseThrow(() -> new NotFoundException("Order not found"));
        if (!order.status().equals("received")) {
            throw new BadRequestException("Order must be received before packing");
        }

        if (order.worker() == null || order.worker().id() == null) {
            throw new BadRequestException("Order must have a worker assigned before packing");
        }

        orderDao.packOrder(order);
    }

    @Transactional
    public Order getOrderById(Long orderId) {
        Order order = orderDao.getOrderById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        List<ProductInOrder> productsInOrder = productDao.getProductsInOrder(order.id());
        BigDecimal totalPrice = new BigDecimal(0);
        for (ProductInOrder productInOrder : productsInOrder) {
            totalPrice = totalPrice.add(productInOrder.price());
        }
        return new Order(order.id(), order.customer(), order.dateProcessed(), order.worker(), order.status(), order.dateReceived(), totalPrice, productsInOrder);
    }
}
