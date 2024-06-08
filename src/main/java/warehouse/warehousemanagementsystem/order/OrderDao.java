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
                SELECT * FROM orders
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
                order.status(),
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

}
