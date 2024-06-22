package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.stereotype.Service;

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

    public List<OrdersHistory> getOrdersByWorker(Long workerId) {
        return ordersHistoryDao.getOrdersByWorker(workerId);
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
}
