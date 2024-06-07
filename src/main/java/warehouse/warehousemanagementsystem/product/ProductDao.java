package warehouse.warehousemanagementsystem.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public Product getProductById(Long id) {
        var sql = """
                SELECT * FROM products
                WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(
                sql,
                new ProductMapper(),
                id
        );
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
}
