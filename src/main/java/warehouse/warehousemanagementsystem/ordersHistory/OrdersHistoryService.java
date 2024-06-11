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
}
