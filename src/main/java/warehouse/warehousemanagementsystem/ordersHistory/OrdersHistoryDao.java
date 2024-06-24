package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.warehousemanagementsystem.order.Order;
import warehouse.warehousemanagementsystem.order.OrderMapper;
import warehouse.warehousemanagementsystem.product.ProductInOrder;
import warehouse.warehousemanagementsystem.product.ProductInOrderMapper;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class OrdersHistoryDao {
    private final JdbcTemplate jdbcTemplate;
    private final String sqlPreffix;
    private final String sqlSuffix;


    @Autowired
    public OrdersHistoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        sqlPreffix = """
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
                """;
        sqlSuffix = """
                GROUP BY
                    orders_history.id, orders_history.customer_id, customer.name, customer.last_name, customer.email,
                    orders_history.date_processed, orders_history.worker_id, workers.username, orders_history.date_received
                ORDER BY
                    orders_history.id;
                """;
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

    public Optional<Order> getOrderById(Long orderId) {
        var sql = """
                SELECT DISTINCT orders_history.id as order_history_id,
                products_orders_history.order_id as order_id, orders_history.date_processed, 'processed' AS status, orders_history.date_received,
                customers.id as customer_id, customers.email as customer_email, customers.name as customer_name, customers.last_name as customer_last_name,
                addresses.id as address_id, addresses.street, addresses.house_nr, addresses.postal_code, addresses.city, addresses.country,
                workers.id as worker_id, workers.username as worker_username, workers.name as worker_name, workers.last_name as worker_last_name, workers.role as worker_role
                FROM orders_history
                LEFT JOIN products_orders_history ON orders_history.id = products_orders_history.order_id
                LEFT JOIN customers ON orders_history.customer_id = customers.id
                LEFT JOIN addresses ON customers.address_id = addresses.id
                LEFT JOIN workers ON orders_history.worker_id = workers.id
                WHERE orders_history.id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new OrderMapper(),
                orderId
        ).stream().findFirst();
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
        var sql = sqlPreffix.concat("\n").concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryViewMapper()
        );
    }

    public List<OrdersHistoryView> getOrdersHistViewsByWorkerUsernameSubstring(String usernameSubstring) {
        var sql = sqlPreffix.
                concat("""
                WHERE worker_id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))
                """).
                concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryViewMapper(),
                "%" + usernameSubstring + "%"
        );
    }

    public List<OrdersHistoryView> getOrdersHistViewsByCustomerEmailSubstring(String emailSubstring) {
        var sql = sqlPreffix.
                concat("""
                WHERE customer_id IN (SELECT id FROM customers WHERE LOWER(customers.email) like LOWER((?)))
                """).
                concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new OrdersHistoryViewMapper(),
                "%" + emailSubstring + "%"
        );
    }

    public List<OrdersHistoryView> getOrdersHistViewsByCustomerEmailWorkerUsernameSubstring(String emailSubstring, String usernameSubstring) {
        var sql = sqlPreffix.
                concat("""
                WHERE customer_id IN (SELECT id FROM customers WHERE LOWER(customers.email) like LOWER((?))) AND
                    worker_id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))
                """).
                concat(sqlSuffix);

        return jdbcTemplate.query(
                sql,
                new OrdersHistoryViewMapper(),
                "%" + emailSubstring + "%",
                "%" + usernameSubstring + "%"
        );
    }

    public List<OrdersHistoryView> getOrdersHistViewsByOrderId(Long orderId) {
        var sql = sqlPreffix.
                concat("""
                WHERE orders_history.id = ?
                """).
                concat(sqlSuffix);

        return jdbcTemplate.query(
                sql,
                new OrdersHistoryViewMapper(),
                orderId
        );

    }

    public List<Order> getOrders(String customerEmail, String workerUsername) {
        if (customerEmail == null) {
            customerEmail = "";
        }
        if (workerUsername == null) {
            workerUsername = "";
        }
        var sql = """
                SELECT DISTINCT orders_history.id as order_history_id,
                products_orders_history.order_id as order_id, orders_history.date_processed, 'processed' AS status, orders_history.date_received,
                customers.id as customer_id, customers.email as customer_email, customers.name as customer_name, customers.last_name as customer_last_name,
                addresses.id as address_id, addresses.street, addresses.house_nr, addresses.postal_code, addresses.city, addresses.country,
                workers.id as worker_id, workers.username as worker_username, workers.name as worker_name, workers.last_name as worker_last_name, workers.role as worker_role
                FROM orders_history
                LEFT JOIN products_orders_history ON orders_history.id = products_orders_history.order_id
                LEFT JOIN customers ON orders_history.customer_id = customers.id
                LEFT JOIN addresses ON customers.address_id = addresses.id
                LEFT JOIN workers ON orders_history.worker_id = workers.id
                WHERE (LOWER(workers.username) LIKE LOWER(?) OR (worker_id ISNULL AND '' = ? )) AND
                (LOWER(customers.email) LIKE LOWER(?) OR (customer_id ISNULL AND '' = ?))
                ORDER BY orders_history.id
                """;
        return jdbcTemplate.query(
                sql,
                new OrderMapper(),
                "%" + workerUsername + "%",
                workerUsername,
                "%" + customerEmail + "%",
                customerEmail
        );
    }

    public List<ProductInOrder> getProductsInOrder(Long id) {
        var sql = """
         SELECT c.id as category_id, c.name as category_name,
         p.id as id, p.name as name, p.price, poh.amount
         FROM orders_history
         LEFT JOIN products_orders_history poh on orders_history.id = poh.order_id
         LEFT JOIN products p on poh.product_id = p.id
         LEFT JOIN categories c on p.category_id = c.id
         WHERE orders_history.id = ?
         """;
        return jdbcTemplate.query(
                sql,
                new ProductInOrderMapper(),
                id
        );
    }
}
