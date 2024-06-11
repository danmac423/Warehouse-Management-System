package warehouse.warehousemanagementsystem.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Order> getAllOrders() {
        var sql = """
                SELECT * 
                FROM orders
                """;
        return jdbcTemplate.query(
                sql,
                new OrderMapper()
        );
    }

    public int addOrder(Order order) {
        var sql = """
                INSERT INTO orders (customer_id, date_processed, worker_id, status, date_received)
                VALUES (?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                order.customerId(),
                order.dateProcessed(),
                order.workerId(),
                "received",
                order.dateReceived()
        );
    }

    public int updateOrder(Order order) {
        var sql = """
                UPDATE orders
                SET customer_id = ?, date_processed = ?, worker_id = ?, status = ?, date_received = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                order.customerId(),
                order.dateProcessed(),
                order.workerId(),
                order.status(),
                order.dateReceived(),
                order.id()
        );
    }

    public List<Order> getOrdersByWorker(Long workerId) {
        var sql = """
                SELECT * FROM orders
                WHERE worker_id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new OrderMapper(),
                workerId
        );
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        var sql = """
                SELECT * FROM orders
                WHERE customer_id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new OrderMapper(),
                customerId
        );
    }

    public int packOrder(Order order) {
        var sql = """
                UPDATE orders
                SET status = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                "processed",
                order.id()
        );
    }

    public int assignWorker(Order order) {
        var sql = """
                UPDATE orders
                SET worker_id = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                order.workerId(),
                order.id()
        );
    }

}
