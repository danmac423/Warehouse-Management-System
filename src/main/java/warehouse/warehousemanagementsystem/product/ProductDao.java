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
                SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id
                ORDER BY id
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper()
        );
    }

    public Optional<Product> getProductById(Long id) {
        var sql = """
                SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id
                WHERE p.id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                id
        ).stream().findFirst();
    }

    public Optional<Product> getProductByName(Product product) {
        var sql = """
                SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id
                WHERE p.name = ?
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
                SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id
                WHERE p.category_id = ?
                ORDER BY p.id
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                categoryId
        );
    }

    public List<Product> getProductsBySubstring(String substring) {
        var sql = """
                SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id
                WHERE LOWER(p.name) LIKE LOWER(?)
                ORDER BY p.id
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                "%" + substring + "%"
        );
    }

    public List<Product> getProductsByCategoryAndSubstring(Long categoryId, String substring) {
        var sql = """
                SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id
                WHERE p.category_id = ? AND LOWER(p.name) LIKE LOWER(?)
                ORDER BY id
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                categoryId,
                "%" + substring + "%"
        );
    }


    public List<ProductInOrder> getProductsByOrder(Long orderId) {
        var sql = """
                SELECT products.id, products.name, products.price, categories.name as category_name, products_orders.amount
                FROM products
                JOIN products_orders ON products.id = products_orders.product_id JOIN categories ON products.category_id = categories.id
                WHERE products_orders.order_id = ?
                ORDER BY products.id
                """;
        return jdbcTemplate.query(
                sql,
                new ProductInOrderMapper(),
                orderId
        );
    }

    public List<ProductInOrder> getProductsByOrderHistory(Long orderId) {
        var sql = """
                SELECT products.id, products.name, products.price, categories.name as category_name, products_orders_history.amount
                FROM products
                JOIN products_orders_history ON products.id = products_orders_history.product_id JOIN categories ON products.category_id = categories.id
                WHERE products_orders_history.order_id = ?
                ORDER BY products.id
                """;
        return jdbcTemplate.query(
                sql,
                new ProductInOrderMapper(),
                orderId
        );
    }
}
