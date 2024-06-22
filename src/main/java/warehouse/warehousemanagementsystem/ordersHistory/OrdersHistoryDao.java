package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.warehousemanagementsystem.orderView.OrderView;
import warehouse.warehousemanagementsystem.orderView.OrderViewGeneral;
import warehouse.warehousemanagementsystem.orderView.OrderViewGeneralMapper;
import warehouse.warehousemanagementsystem.orderView.OrderViewMapper;

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

    public List<OrdersHistory> getOrdersByCustomer(String email) {
        var sql = """
                SELECT *
                FROM orders_history JOIN customers ON orders_history.customer_id = customers.id
                WHERE LOWER(customers.email) LIKE LOWER(?)
                ORDER BY orders_history.id
                """;
        String toSearch = "%" +  email + "%";
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryMapper(),
                toSearch
        );
    }

    // zapytac czy musze robic kolejny view
    public List<OrdersHistoryView> getAllOrdersViews() {
        var sql = """
                SELECT
                    orders_history.id,
                    orders_history.customer_id,
                    customer.name AS customer_name,
                    customer.last_name,
                    customer.email,
                    orders_history.date_processed,
                    orders_history.worker_id,
                	workers.username,
                    orders_history.date_received,
                    COALESCE(SUM(product.price * products_orders_history.amount), 0) AS total_price
                FROM
                    orders_history
                LEFT JOIN
                    products_orders_history ON orders_history.id = products_orders_history.order_id
                LEFT JOIN
                    products product ON products_orders_history.product_id = product.id
                LEFT JOIN
                    customers customer ON orders_history.customer_id = customer.id
                LEFT JOIN
                    workers ON orders_history.worker_id = workers.id
                GROUP BY
                    orders_history.id, orders_history.customer_id, customer.name, customer.last_name, customer.email,
                    orders_history.date_processed, orders_history.worker_id, workers.username, orders_history.date_received
                ORDER BY
                    orders_history.id;
                """;
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryViewMapper()
        );
    }

    public List<OrdersHistoryView> getOrdersHistViewsByWorkerUsernameSubstring(String usernameSubstring) {
        var sql = """
                SELECT
                    orders_history.id,
                    orders_history.customer_id,
                    customer.name AS customer_name,
                    customer.last_name,
                    customer.email,
                    orders_history.date_processed,
                    orders_history.worker_id,
                	workers.username,
                    orders_history.date_received,
                    COALESCE(SUM(product.price * products_orders_history.amount), 0) AS total_price
                FROM
                    orders_history
                LEFT JOIN
                    products_orders_history ON orders_history.id = products_orders_history.order_id
                LEFT JOIN
                    products product ON products_orders_history.product_id = product.id
                LEFT JOIN
                    customers customer ON orders_history.customer_id = customer.id
                LEFT JOIN
                    workers ON orders_history.worker_id = workers.id
                GROUP BY
                    orders_history.id, orders_history.customer_id, customer.name, customer.last_name, customer.email,
                    orders_history.date_processed, orders_history.worker_id, workers.username, orders_history.date_received
                ORDER BY
                    orders_history.id;
                """;
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryViewMapper(),
                "%" + usernameSubstring + "%"
        );
    }

    public List<OrdersHistoryView> getOrdersHistViewsByCustomerEmailSubstring(String emailSubstring) {
        var sql = """
                SELECT
                    orders_history.id,
                    orders_history.customer_id,
                    customer.name AS customer_name,
                    customer.last_name,
                    customer.email,
                    orders_history.date_processed,
                    orders_history.worker_id,
                	workers.username,
                    orders_history.date_received,
                    COALESCE(SUM(product.price * products_orders_history.amount), 0) AS total_price
                FROM
                    orders_history
                LEFT JOIN
                    products_orders_history ON orders_history.id = products_orders_history.order_id
                LEFT JOIN
                    products product ON products_orders_history.product_id = product.id
                LEFT JOIN
                    customers customer ON orders_history.customer_id = customer.id
                LEFT JOIN
                    workers ON orders_history.worker_id = workers.id
                WHERE customer_id IN (SELECT id FROM customers WHERE LOWER(customers.email) like LOWER((?)))
                GROUP BY
                    orders_history.id, orders_history.customer_id, customer.name, customer.last_name, customer.email,
                    orders_history.date_processed, orders_history.worker_id, workers.username, orders_history.date_received
                ORDER BY
                    orders_history.id;
                """;
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryViewMapper(),
                "%" + emailSubstring + "%"
        );
    }

    public List<OrdersHistoryView> getOrdersHistViewsByCustomerEmailWorkerUsernameSubstring(String emailSubstring, String usernameSubstring) {
        var sql = """
                SELECT
                    orders_history.id,
                    orders_history.customer_id,
                    customer.name AS customer_name,
                    customer.last_name,
                    customer.email,
                    orders_history.date_processed,
                    orders_history.worker_id,
                	workers.username,
                    orders_history.date_received,
                    COALESCE(SUM(product.price * products_orders_history.amount), 0) AS total_price
                FROM
                    orders_history
                LEFT JOIN
                    products_orders_history ON orders_history.id = products_orders_history.order_id
                LEFT JOIN
                    products product ON products_orders_history.product_id = product.id
                LEFT JOIN
                    customers customer ON orders_history.customer_id = customer.id
                LEFT JOIN
                    workers ON orders_history.worker_id = workers.id
                WHERE customer_id IN (SELECT id FROM customers WHERE LOWER(customers.email) like LOWER((?))) AND
                    worker_id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))
                GROUP BY
                    orders_history.id, orders_history.customer_id, customer.name, customer.last_name, customer.email,
                    orders_history.date_processed, orders_history.worker_id, workers.username, orders_history.date_received
                ORDER BY
                    orders_history.id;
                """;
        ;
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryViewMapper(),
                "%" + emailSubstring + "%",
                "%" + usernameSubstring + "%"
        );
    }

    public List<OrdersHistoryView> getOrdersHistViewsByOrderId(Long orderId) {
        var sql = """
                SELECT
                    orders_history.id,
                    orders_history.customer_id,
                    customer.name AS customer_name,
                    customer.last_name,
                    customer.email,
                    orders_history.date_processed,
                    orders_history.worker_id,
                	workers.username,
                    orders_history.date_received,
                    COALESCE(SUM(product.price * products_orders_history.amount), 0) AS total_price
                FROM
                    orders_history
                LEFT JOIN
                    products_orders_history ON orders_history.id = products_orders_history.order_id
                LEFT JOIN
                    products product ON products_orders_history.product_id = product.id
                LEFT JOIN
                    customers customer ON orders_history.customer_id = customer.id
                LEFT JOIN
                    workers ON orders_history.worker_id = workers.id
                WHERE orders_history.id = ?
                GROUP BY
                    orders_history.id, orders_history.customer_id, customer.name, customer.last_name, customer.email,
                    orders_history.date_processed, orders_history.worker_id, workers.username, orders_history.date_received
                ORDER BY
                    orders_history.id;
                """;
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryViewMapper(),
                orderId
        );

    }
}
