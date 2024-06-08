package warehouse.warehousemanagementsystem.order;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.product.Product;

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
        if (order.customerId() == null
                || order.workerId() == null
                || order.status() == null
                || order.dateReceived() == null) {
            throw new BadRequestException("All fields are required");
        }
        if (orderDao.addOrder(order) != 1) {
            throw new BadRequestException("Failed to add order");
        }
    }

    public void updateOrder(Order order) {
        if (order.customerId() == null
                || order.workerId() == null
                || order.status().isEmpty()
                || order.dateReceived() == null) {
            throw new BadRequestException("All fields are required");
        }
        if (orderDao.updateOrder(order) != 1) {
            throw new BadRequestException("Failed to update order");
        }
    }
}
