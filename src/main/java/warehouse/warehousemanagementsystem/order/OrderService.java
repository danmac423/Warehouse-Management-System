package warehouse.warehousemanagementsystem.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.NotFoundException;
import warehouse.warehousemanagementsystem.product.ProductDao;
import warehouse.warehousemanagementsystem.product.ProductInOrderDto;

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
    public List<OrderDto> getOrders(String workerUsername, String customerEmail, String status, Long workerId) {
        List<OrderDto> orders = orderDao.getOrders(workerUsername, customerEmail, status, workerId);

        List<OrderDto> completedOrders = new ArrayList<>();
        for (OrderDto order : orders) {
            List<ProductInOrderDto> productsInOrder = productDao.getProductsInOrder(order.id());
            BigDecimal totalPrice = new BigDecimal(0);
            for (ProductInOrderDto productInOrder : productsInOrder) {
                BigDecimal itemPrice = productInOrder.price();
                BigDecimal amount = BigDecimal.valueOf(productInOrder.amount());
                totalPrice = totalPrice.add(itemPrice.multiply(amount));
            }
            order = new OrderDto(order.id(), order.customer(), order.dateProcessed(), order.worker(), order.status(), order.dateReceived(), totalPrice, productsInOrder);
            completedOrders.add(order);
        }
        return completedOrders;
    }

    @Transactional
    public void packOrder(OrderDto order) {
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
    public OrderDto getOrderById(Long orderId) {
        OrderDto order = orderDao.getOrderById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        List<ProductInOrderDto> productsInOrder = productDao.getProductsInOrder(order.id());
        BigDecimal totalPrice = new BigDecimal(0);
        for (ProductInOrderDto productInOrder : productsInOrder) {
            BigDecimal itemPrice = productInOrder.price();
            BigDecimal amount = BigDecimal.valueOf(productInOrder.amount());
            totalPrice = totalPrice.add(itemPrice.multiply(amount));
        }
        return new OrderDto(order.id(), order.customer(), order.dateProcessed(), order.worker(), order.status(), order.dateReceived(), totalPrice, productsInOrder);
    }

    @Transactional
    public OrderDto assignOrder(OrderDto order) {
        OrderDto currentOrder = orderDao.getOrderById(order.id()).orElseThrow(() -> new NotFoundException("Order not found"));
        if (!currentOrder.status().equals("received")) {
            throw new BadRequestException("Order must be received before assigning");
        }

        if (order.worker() == null || order.worker().id() == null) {
            throw new BadRequestException("You must provide a worker to assign the order to");
        }

        return orderDao.assignOrder(order);
    }
}
