package warehouse.warehousemanagementsystem.orderView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderViewDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderViewDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OrderViewGeneral> getAllOrdersViews() {
        var sql = """
                SELECT
                    orders.id,
                    customer.email,
                    orders.date_processed,
                    orders.status,
                    orders.date_received,
                    workers.username,
                    COALESCE(SUM(product.price * products_orders.amount), 0) AS total_price
                FROM
                    orders
                LEFT JOIN
                    products_orders ON orders.id = products_orders.order_id
                LEFT JOIN
                    products product ON products_orders.product_id = product.id
                LEFT JOIN
                    customers customer ON orders.customer_id = customer.id
                LEFT JOIN
                    workers ON orders.worker_id = workers.id
                GROUP BY
                    orders.id, orders.customer_id, customer.name, customer.last_name, customer.email,
                    orders.date_processed, orders.worker_id, orders.status, orders.date_received
                ORDER BY
                    orders.id;
                """;
        return jdbcTemplate.query(
                sql,
                new OrderViewGeneralMapper()
        );
    }

    public List<OrderView> getOrdersViewsByWorkerUsernameSubstring(String usernameSubstring) {
        var sql = """
                SELECT
                    orders.id,
                    orders.customer_id,
                    customer.name AS customer_name,
                    customer.last_name,
                    customer.email,
                    orders.date_processed,
                    orders.worker_id,
                	workers.username,
                    orders.status,
                    orders.date_received,
                    COALESCE(SUM(product.price * products_orders.amount), 0) AS total_price
                FROM
                    orders
                LEFT JOIN
                    products_orders ON orders.id = products_orders.order_id
                LEFT JOIN
                    products product ON products_orders.product_id = product.id
                LEFT JOIN
                    customers customer ON orders.customer_id = customer.id
                LEFT JOIN
                    workers ON orders.worker_id = workers.id
                WHERE worker_id = (SELECT id FROM workers WHERE LOWER(username) = LOWER(?))
                GROUP BY
                    orders.id, orders.customer_id, customer.name, customer.last_name, customer.email,
                    orders.date_processed, orders.worker_id, orders.status, orders.date_received
                ORDER BY
                    orders.id;
                """;
        return jdbcTemplate.query(
                sql,
                new OrderViewMapper(),
                "%" + usernameSubstring + "%"
        );
    }

    public List<OrderView> getOrdersViewsByCustomerEmailSubstring(String emailSubstring) {
        var sql = """
                SELECT
                    orders.id,
                    orders.customer_id,
                    customer.name AS customer_name,
                    customer.last_name,
                    customer.email,
                    orders.date_processed,
                    orders.worker_id,
                	workers.username,
                    orders.status,
                    orders.date_received,
                    COALESCE(SUM(product.price * products_orders.amount), 0) AS total_price
                FROM
                    orders
                LEFT JOIN
                    products_orders ON orders.id = products_orders.order_id
                LEFT JOIN
                    products product ON products_orders.product_id = product.id
                LEFT JOIN
                    customers customer ON orders.customer_id = customer.id
                LEFT JOIN
                    workers ON orders.worker_id = workers.id
                WHERE customer_id = (SELECT id FROM customers WHERE LOWER(email) = LOWER(?))
                GROUP BY
                    orders.id, orders.customer_id, customer.name, customer.last_name, customer.email,
                    orders.date_processed, orders.worker_id, orders.status, orders.date_received
                ORDER BY
                    orders.id;
                """;
        return jdbcTemplate.query(
                sql,
                new OrderViewMapper(),
                "%" + emailSubstring + "%"
        );
    }

    public List<OrderView> getOrdersViewsByCustomerEmailWorkerUsernameSubstring(String emailSubstring, String usernameSubstring) {
        var sql = """
                SELECT
                    orders.id,
                    orders.customer_id,
                    customer.name AS customer_name,
                    customer.last_name,
                    customer.email,
                    orders.date_processed,
                    orders.worker_id,
                	workers.username,
                    orders.status,
                    orders.date_received,
                    COALESCE(SUM(product.price * products_orders.amount), 0) AS total_price
                FROM
                    orders
                LEFT JOIN
                    products_orders ON orders.id = products_orders.order_id
                LEFT JOIN
                    products product ON products_orders.product_id = product.id
                LEFT JOIN
                    customers customer ON orders.customer_id = customer.id
                LEFT JOIN
                    workers ON orders.worker_id = workers.id
                WHERE customer_id = (SELECT id FROM customers WHERE LOWER(email) = LOWER(?))
                GROUP BY
                    orders.id, orders.customer_id, customer.name, customer.last_name, customer.email,
                    orders.date_processed, orders.worker_id, orders.status, orders.date_received
                ORDER BY
                    orders.id;
                """;
        return jdbcTemplate.query(
                sql,
                new OrderViewMapper(),
                "%" + emailSubstring + "%",
                "%" + usernameSubstring + "%"
        );
    }

    public List<OrderView> getOrdersViewsByWorker(Long workerId) {
        var sql = """
                SELECT
                    orders.id,
                    orders.customer_id,
                    customer.name AS customer_name,
                    customer.last_name,
                    customer.email,
                    orders.date_processed,
                    orders.worker_id,
                	workers.username,
                    orders.status,
                    orders.date_received,
                    COALESCE(SUM(product.price * products_orders.amount), 0) AS total_price
                FROM
                    orders
                LEFT JOIN
                    products_orders ON orders.id = products_orders.order_id
                LEFT JOIN
                    products product ON products_orders.product_id = product.id
                LEFT JOIN
                    customers customer ON orders.customer_id = customer.id
                LEFT JOIN
                    workers ON orders.worker_id = workers.id
                WHERE worker_id = ?
                GROUP BY
                    orders.id, orders.customer_id, customer.name, customer.last_name, customer.email,
                    orders.date_processed, orders.worker_id, orders.status, orders.date_received
                ORDER BY
                    orders.id;
                """;
        return jdbcTemplate.query(
                sql,
                new OrderViewMapper(),
                workerId
        );
    }

    public List<OrderView> getOrdersViewsByCustomer(Long customerId) {
        var sql = """
                SELECT
                    orders.id,
                    orders.customer_id,
                    customer.name AS customer_name,
                    customer.last_name,
                    customer.email,
                    orders.date_processed,
                    orders.worker_id,
                	workers.username,
                    orders.status,
                    orders.date_received,
                    COALESCE(SUM(product.price * products_orders.amount), 0) AS total_price
                FROM
                    orders
                LEFT JOIN
                    products_orders ON orders.id = products_orders.order_id
                LEFT JOIN
                    products product ON products_orders.product_id = product.id
                LEFT JOIN
                    customers customer ON orders.customer_id = customer.id
                LEFT JOIN
                    workers ON orders.worker_id = workers.id
                WHERE customer_id = ?
                GROUP BY
                    orders.id, orders.customer_id, customer.name, customer.last_name, customer.email,
                    orders.date_processed, orders.worker_id, orders.status, orders.date_received
                ORDER BY
                    orders.id;
                """;
        return jdbcTemplate.query(
                sql,
                new OrderViewMapper(),
                customerId
        );
    }

}
