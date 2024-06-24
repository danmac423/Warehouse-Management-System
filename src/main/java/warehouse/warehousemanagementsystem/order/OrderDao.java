package warehouse.warehousemanagementsystem.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderDao {
    private final JdbcTemplate jdbcTemplate;



    @Autowired
    public OrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    Optional<Order> getOrderById(Long id) {
        var sql = """
                SELECT orders.id AS order_id, orders.date_processed, orders.status, orders.date_received,
                       customers.id AS customer_id, customers.name AS customer_name, customers.last_name AS customer_last_name, customers.email AS customer_email,
                       addresses.id AS address_id, addresses.street, addresses.city, addresses.postal_code, addresses.country, addresses.house_nr,
                       workers.id AS worker_id, workers.username AS worker_username, workers.name AS worker_name, workers.last_name AS worker_last_name, workers.role AS worker_role
                FROM orders
                LEFT JOIN customers ON orders.customer_id = customers.id
                LEFT JOIN addresses ON customers.address_id = addresses.id
                LEFT JOIN workers ON orders.worker_id = workers.id
                WHERE orders.id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new OrderMapper(),
                id
        ).stream().findFirst();
    }

    public List<Order> getOrders(String workerUsername, String customerEmail, String status, Long workerId) {
        if (workerUsername == null) {
            workerUsername = "";
        }
        if (customerEmail == null) {
            customerEmail = "";
        }
        if (status == null) {
            status = "";
        }
        if (workerId == null) {
            workerId = 0L;
        }
        var sql = """
                SELECT orders.id AS order_id, orders.date_processed, orders.status, orders.date_received,
                       customers.id AS customer_id, customers.name AS customer_name, customers.last_name AS customer_last_name, customers.email AS customer_email,
                       addresses.id AS address_id, addresses.street, addresses.city, addresses.postal_code, addresses.country, addresses.house_nr,
                       workers.id AS worker_id, workers.username AS worker_username, workers.name AS worker_name, workers.last_name AS worker_last_name, workers.role AS worker_role
                FROM orders
                LEFT JOIN customers ON orders.customer_id = customers.id
                LEFT JOIN addresses ON customers.address_id = addresses.id
                LEFT JOIN workers ON orders.worker_id = workers.id
                WHERE (LOWER(workers.username) LIKE LOWER(?) OR (worker_id ISNULL AND ? = '' )) AND
                      (LOWER(customers.email) LIKE LOWER(?) OR (customer_id ISNULL AND ? = '')) AND
                      LOWER(orders.status) LIKE LOWER(?) AND
                      (? = workers.id OR (? = 0))
                ORDER BY orders.id
                """;
        return jdbcTemplate.query(
                sql,
                new OrderMapper(),
                "%" + workerUsername + "%",
                workerUsername,
                "%" + customerEmail + "%",
                customerEmail,
                "%" + status + "%",
                workerId,
                workerId
        );
    }

    public void packOrder(Order order) {
        var sql = """
                UPDATE orders
                SET status = 'processed'
                WHERE id = ?
                """;
        jdbcTemplate.update(
                sql,
                order.id()
        );
    }

    public Order assignOrder(Order order) {
        var sql = """
                UPDATE orders
                SET worker_id = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(
                sql,
                order.worker().id(),
                order.id()
        );
        return order;
    }
}
