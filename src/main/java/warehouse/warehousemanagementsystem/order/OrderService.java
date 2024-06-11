package warehouse.warehousemanagementsystem.order;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.DatabaseException;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    private final OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public List<Order> getAllOrders() {
        return orderDao.getAllOrders();
    }

    public void addOrder(Order order) {
        long currentMilliseconds = System.currentTimeMillis();
        if (order.customerId() == null
                || order.dateReceived() == null) {
            throw new BadRequestException("All fields are required");
        }
        if (order.dateReceived().before(new Date(currentMilliseconds))){
            throw new BadRequestException("The date received must be in the future");
        }
        if (orderDao.addOrder(order) != 1) {
            throw new DatabaseException("Failed to add order");
        }
    }

    public void updateOrder(Order order) {
        long currentMilliseconds = System.currentTimeMillis();
        if (order.customerId() == null
                || order.dateReceived() == null) {
            throw new BadRequestException("All fields are required");
        }
        if (order.dateReceived().before(new Date(currentMilliseconds))){
            throw new BadRequestException("The date received must be in the future");
        }
        if (orderDao.updateOrder(order) != 1) {
            throw new DatabaseException("Failed to update order");
        }
    }

    public List<Order> getOrdersByWorker(Long workerId) {
        return orderDao.getOrdersByWorker(workerId);
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderDao.getOrdersByCustomer(customerId);
    }

    public void packOrder(Order order) {
        if (!order.status().equals("received")) {
            throw new BadRequestException("Order must be received before packing");
        }
        if (order.workerId() == null) {
            throw new BadRequestException("Order must have a worker assigned before packing");
        }
        if (orderDao.packOrder(order) != 1) {
            throw new DatabaseException("Failed to pack order");
        }
    }

    public void assignWorker(Order order) {
        if (orderDao.assignWorker(order) != 1) {
            throw new DatabaseException("Failed to assign worker");
        }
    }
}
