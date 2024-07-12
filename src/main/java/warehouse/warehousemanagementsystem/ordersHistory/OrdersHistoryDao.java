package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.warehousemanagementsystem.order.OrderDto;
import warehouse.warehousemanagementsystem.order.OrderMapper;
import warehouse.warehousemanagementsystem.product.ProductInOrderDto;
import warehouse.warehousemanagementsystem.product.ProductInOrderMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class OrdersHistoryDao {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public OrdersHistoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Optional<OrderDto> getOrderById(Long orderId) {
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

    public List<OrderDto> getOrders(String customerEmail, String workerUsername) {
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

    public List<ProductInOrderDto> getProductsInOrder(Long id) {
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
