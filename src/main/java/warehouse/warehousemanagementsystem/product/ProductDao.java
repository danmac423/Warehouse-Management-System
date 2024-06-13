package warehouse.warehousemanagementsystem.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Product> getAllProducts() {
        var sql = """
                SELECT * FROM products
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper()
        );
    }

    public Optional<Product> getProductById(Long id) {
        var sql = """
                SELECT * FROM products
                WHERE id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                id
        ).stream().findFirst();
    }

    public Optional<Product> getProductByName(Product product) {
        var sql = """
                SELECT * FROM products
                WHERE name = ?
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                product.name()
        ).stream().findFirst();
    }

    public int addProduct(Product product) {
        var sql = """
                INSERT INTO products (name, price, category_id, stock)
                VALUES (?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                product.name(),
                product.price(),
                product.categoryId(),
                product.stock()
        );
    }

    public int deleteProduct(Long id) {
        var sql = """
                DELETE FROM products
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                id
        );
    }

    public int updateProduct(Product product) {
        var sql = """
                UPDATE products
                SET name = ?, price = ?, category_id = ?, stock = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                product.name(),
                product.price(),
                product.categoryId(),
                product.stock(),
                product.id()
        );
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        var sql = """
                SELECT * FROM products
                WHERE category_id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                categoryId
        );
    }

    public List<ProductInOrder> getProductsByOrder(Long orderId) {
        var sql = """
                SELECT products.id, products.name, products.price, categories.name as category_name, products_orders.amount
                FROM products
                JOIN products_orders ON products.id = products_orders.product_id JOIN categories ON products.category_id = categories.id
                WHERE products_orders.order_id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new ProductInOrderMapper(),
                orderId
        );
    }

    public List<ProductInOrder> getProductsByOrderHistory(Long orderHistoryId) {
        var sql = """
                SELECT products.id, product.name, products.id, products.name, products.price, 
                categories.name as category_name, products_orders.amount
                FROM products 
                JOIN products_orders_history ON products.id = products_orders_history.product_id 
                JOIN categories ON products.category_id = categories.id
                WHERE products_orders_history.order_id = ?;
                """;
        return jdbcTemplate.query(
                sql,
                new ProductInOrderMapper(),
                orderHistoryId
        );
    }
}
