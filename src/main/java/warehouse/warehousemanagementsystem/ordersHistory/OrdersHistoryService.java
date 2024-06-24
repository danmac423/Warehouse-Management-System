package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.NotFoundException;

import java.util.Date;
import java.util.List;

@Service
public class OrdersHistoryService {
    private final OrdersHistoryDao ordersHistoryDao;

    public OrdersHistoryService(OrdersHistoryDao ordersHistoryDao) {
        this.ordersHistoryDao = ordersHistoryDao;
    }

    public List<OrdersHistory> getAllOrders() {
        return ordersHistoryDao.getAllOrders();
    }

    public OrdersHistory getOrderById(Long orderId) {
        try {
            return ordersHistoryDao.getOrderById(orderId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Order not found");
        }
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
}
