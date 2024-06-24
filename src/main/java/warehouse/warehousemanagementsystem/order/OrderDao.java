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


//    public List<Order> getAllOrders() {
//        var sql = """
//                SELECT orders.id, orders.customer_id, orders.date_processed, orders.worker_id, orders.status, orders.date_received, COALESCE(SUM(products.price * products_orders.amount)) as total_price
//                FROM orders LEFT JOIN products_orders on orders.id = products_orders.order_id LEFT JOIN products on products_orders.product_id = products.id
//                GROUP BY orders.id
//                ORDER BY orders.id
//                """;
//        return jdbcTemplate.query(
//                sql,
//                new OrderMapper()
//        );
//    }
//
//    public List<Order> getOrdersByWorkerUsernameSubstring(String usernameSubstring) {
//        var sql = """
//                SELECT orders.id, orders.customer_id, orders.date_processed, orders.worker_id, orders.status, orders.date_received, COALESCE(SUM(products.price * products_orders.amount)) as total_price
//                FROM orders LEFT JOIN products_orders on orders.id = products_orders.order_id LEFT JOIN products on products_orders.product_id = products.id
//                WHERE worker_id = (SELECT id FROM workers WHERE LOWER(username) = LOWER(?))
//                GROUP BY orders.id
//                ORDER BY orders.id
//                """;
//        return jdbcTemplate.query(
//                sql,
//                new OrderMapper(),
//                "%" + usernameSubstring + "%"
//        );
//    }
//
//    public List<Order> getOrdersByCustomerEmailSubstring(String emailSubstring) {
//        var sql = """
//                SELECT orders.id, orders.customer_id, orders.date_processed, orders.worker_id, orders.status, orders.date_received, COALESCE(SUM(products.price * products_orders.amount)) as total_price
//                FROM orders LEFT JOIN products_orders on orders.id = products_orders.order_id LEFT JOIN products on products_orders.product_id = products.id
//                WHERE customer_id = (SELECT id FROM customers WHERE LOWER(email) = LOWER(?))
//                GROUP BY orders.id
//                ORDER BY orders.id
//                """;
//        return jdbcTemplate.query(
//                sql,
//                new OrderMapper(),
//                "%" + emailSubstring + "%"
//        );
//    }
//
//    public List<Order> getOrdersByCustomerEmailWorkerUsernameSubstring(String emailSubstring, String usernameSubstring) {
//        var sql = """
//                SELECT orders.id, orders.customer_id, orders.date_processed, orders.worker_id, orders.status, orders.date_received, COALESCE(SUM(products.price * products_orders.amount)) as total_price
//                FROM orders LEFT JOIN products_orders on orders.id = products_orders.order_id LEFT JOIN products on products_orders.product_id = products.id
//                WHERE customer_id = (SELECT id FROM customers WHERE LOWER(email) = LOWER(?)) AND worker_id = (SELECT id FROM workers WHERE LOWER(username) = LOWER(?))
//                GROUP BY orders.id
//                ORDER BY orders.id
//                """;
//        return jdbcTemplate.query(
//                sql,
//                new OrderMapper(),
//                "%" + emailSubstring + "%",
//                "%" + usernameSubstring + "%"
//        );
//    }
//
//    public int addOrder(Order order) {
//        var sql = """
//                INSERT INTO orders (customer_id, date_processed, worker_id, status, date_received)
//                VALUES (?, ?, ?, ?, ?)
//                """;
//        return jdbcTemplate.update(
//                sql,
//                order.customerId(),
//                order.dateProcessed(),
//                order.workerId(),
//                "received",
//                order.dateReceived()
//        );
//    }
//
//    public int updateOrder(Order order) {
//        var sql = """
//                UPDATE orders
//                SET customer_id = ?, date_processed = ?, worker_id = ?, status = ?, date_received = ?
//                WHERE id = ?
//                """;
//        return jdbcTemplate.update(
//                sql,
//                order.customerId(),
//                order.dateProcessed(),
//                order.workerId(),
//                order.status(),
//                order.dateReceived(),
//                order.id()
//        );
//    }
//
//    public List<Order> getOrdersByWorker(Long workerId) {
//        var sql = """
//                SELECT * FROM orders
//                WHERE worker_id = ?
//                """;
//        return jdbcTemplate.query(
//                sql,
//                new OrderMapper(),
//                workerId
//        );
//    }
//
//    public List<Order> getOrdersByCustomer(Long customerId) {
//        var sql = """
//                SELECT * FROM orders
//                WHERE customer_id = ?
//                """;
//        return jdbcTemplate.query(
//                sql,
//                new OrderMapper(),
//                customerId
//        );
//    }
//
//    public int packOrder(Order order) {
//        var sql = """
//                UPDATE orders
//                SET status = ?
//                WHERE id = ?
//                """;
//        return jdbcTemplate.update(
//                sql,
//                "processed",
//                order.id()
//        );
//    }
//
//    public int assignWorker(Order order) {
//        var sql = """
//                UPDATE orders
//                SET worker_id = ?
//                WHERE id = ?
//                """;
//        return jdbcTemplate.update(
//                sql,
//                order.workerId(),
//                order.id()
//        );
//    }

}
