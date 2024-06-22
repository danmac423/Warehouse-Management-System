package warehouse.warehousemanagementsystem.orderView;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.DatabaseException;
import warehouse.warehousemanagementsystem.exception.NotFoundException;
import warehouse.warehousemanagementsystem.order.Order;
import warehouse.warehousemanagementsystem.order.OrderDao;

import java.util.Date;
import java.util.List;

@Service
public class OrderViewService {
    private final OrderViewDao orderVDao;

    public OrderViewService(OrderViewDao orderVDao) {
        this.orderVDao = orderVDao;
    }

    public List<OrderViewGeneral> getAllOrders() {
        return orderVDao.getAllOrdersViews();
    }

    public List<OrderView> getOrdersByWorkerUsernameSubstring(String usernameSubstring) {
        var orders =  orderVDao.getOrdersViewsByWorkerUsernameSubstring(usernameSubstring);
        if (orders.isEmpty()) {
            throw new NotFoundException("No orders found");
        }
        return orders;
    }

    public List<OrderView> getOrdersByCustomerEmailSubstring(String emailSubstring) {
        var orders =  orderVDao.getOrdersViewsByCustomerEmailSubstring(emailSubstring);
        if (orders.isEmpty()) {
            throw new NotFoundException("No orders found");
        }
        return orders;
    }

    public List<OrderView> getOrdersByCustomerEmailWorkerUsernameSubstring(String emailSubstring, String usernameSubstring) {
        var orders =  orderVDao.getOrdersViewsByCustomerEmailWorkerUsernameSubstring(emailSubstring, usernameSubstring);
        if (orders.isEmpty()) {
            throw new NotFoundException("No orders found");
        }
        return orders;
    }

    public List<OrderView> getOrdersViewsByWorker(Long workerId) {
        return orderVDao.getOrdersViewsByWorker(workerId);
    }

    public List<OrderView> getOrdersViewsByCustomer(Long customerId) {
        return orderVDao.getOrdersViewsByCustomer(customerId);
    }

    public List<OrderView> getordersViewsByOrderId(Long orderId) { return orderVDao.getOrdersViewsByOrderId(orderId); }

}
