package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.warehousemanagementsystem.order.Order;

import java.util.Date;
import java.util.List;

@Repository
public class OrdersHistoryDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrdersHistoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OrdersHistory> getAllOrders() {
        var sql = """
                SELECT * FROM orders_history;
                """;
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryMapper()
        );
    }

    public List<OrdersHistory> getOrdersByWorker(Long workerId) {
        var sql = """
                SELECT * FROM orders_history
                WHERE worker_id = ?;
                """;
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryMapper(),
                workerId
        );
    }

    public List<OrdersHistory> getOrderByWorkerWithDates(Long workerId, Date processedDateMin, Date processedDateMax) {
        var sql = """
                SELECT * FROM orders_history
                WHERE worker_id = ? AND date_processed BETWEEN ? and ?; 
                """;
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryMapper(),
                workerId,
                processedDateMin,
                processedDateMax
        );
    }

    public List<OrdersHistory> getOrdersByCustomer(String email) {
        var sql = """
                SELECT * 
                FROM orders_history JOIN customers ON orders_history.customer_id = customers.id
                WHERE LOWER(customers.email) LIKE LOWER(?)
                """;
        String toSearch = "%" +  email + "%";
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryMapper(),
                toSearch
        );
    }
}
