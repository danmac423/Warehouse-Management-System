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
//
//    public List<Order> getOrdersByWorkerUsernameSubstring(String usernameSubstring) {
//        var orders =  orderDao.getOrdersByWorkerUsernameSubstring(usernameSubstring);
//        if (orders.isEmpty()) {
//            throw new NotFoundException("No orders found");
//        }
//        return orders;
//    }
//
//    public List<Order> getOrdersByCustomerEmailSubstring(String emailSubstring) {
//        var orders =  orderDao.getOrdersByCustomerEmailSubstring(emailSubstring);
//        if (orders.isEmpty()) {
//            throw new NotFoundException("No orders found");
//        }
//        return orders;
//    }
//
//    public List<Order> getOrdersByCustomerEmailWorkerUsernameSubstring(String emailSubstring, String usernameSubstring) {
//        var orders =  orderDao.getOrdersByCustomerEmailWorkerUsernameSubstring(emailSubstring, usernameSubstring);
//        if (orders.isEmpty()) {
//            throw new NotFoundException("No orders found");
//        }
//        return orders;
//    }
//
//    public void addOrder(Order order) {
//        long currentMilliseconds = System.currentTimeMillis();
//        if (order.customerId() == null
//                || order.dateReceived() == null) {
//            throw new BadRequestException("All fields are required");
//        }
//        if (order.dateReceived().before(new Date(currentMilliseconds))){
//            throw new BadRequestException("The date received must be in the future");
//        }
//        if (orderDao.addOrder(order) != 1) {
//            throw new DatabaseException("Failed to add order");
//        }
//    }
//
//    public void updateOrder(Order order) {
//        long currentMilliseconds = System.currentTimeMillis();
//        if (order.customerId() == null
//                || order.dateReceived() == null) {
//            throw new BadRequestException("All fields are required");
//        }
//        if (order.dateReceived().before(new Date(currentMilliseconds))){
//            throw new BadRequestException("The date received must be in the future");
//        }
//        if (orderDao.updateOrder(order) != 1) {
//            throw new DatabaseException("Failed to update order");
//        }
//    }
//
//    public List<Order> getOrdersByWorker(Long workerId) {
//        return orderDao.getOrdersByWorker(workerId);
//    }
//
//    public List<Order> getOrdersByCustomer(Long customerId) {
//        return orderDao.getOrdersByCustomer(customerId);
//    }
//
//    public void packOrder(Order order) {
//        if (!order.status().equals("received")) {
//            throw new BadRequestException("Order must be received before packing");
//        }
//        if (order.workerId() == null || order.workerId().equals(0L)) {
//            throw new BadRequestException("Order must have a worker assigned before packing");
//        }
//        if (orderDao.packOrder(order) != 1) {
//            throw new DatabaseException("Failed to pack order");
//        }
//    }
//
//    public void assignWorker(Order order) {
//        if (orderDao.assignWorker(order) != 1) {
//            throw new DatabaseException("Failed to assign worker");
//        }
//    }
//    public List<Order> getAllOrdersViews() {
//        return orderDao.getAllOrders();
//    }

}
