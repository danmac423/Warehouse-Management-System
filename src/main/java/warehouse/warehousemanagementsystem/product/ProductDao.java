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
                SELECT
                    products.id AS product_id,
                    products.name AS product_name,
                    products.price AS product_price,
                    products.stock AS product_stock,
                    categories.id AS category_id,
                    categories.name AS category_name,
                    (SELECT COUNT(*) FROM products p WHERE p.category_id = categories.id) AS product_count
                FROM
                    products
                        LEFT JOIN
                    categories
                    ON
                        products.category_id = categories.id
                ORDER BY
                products.id;
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper()
        );
    }

    public Optional<Product> getProductById(Long id) {
        var sql = """
                SELECT
                    products.id AS product_id,
                    products.name AS product_name,
                    products.price AS product_price,
                    products.stock AS product_stock,
                    categories.id AS category_id,
                    categories.name AS category_name,
                    (SELECT COUNT(*) FROM products p WHERE p.category_id = categories.id) AS product_count
                FROM
                    products
                        LEFT JOIN
                    categories
                    ON
                        products.category_id = categories.id
                WHERE products.id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                id
        ).stream().findFirst();
    }

    public Optional<Product> getProductByName(Product product) {
        var sql = """
                SELECT
                    products.id AS product_id,
                    products.name AS product_name,
                    products.price AS product_price,
                    products.stock AS product_stock,
                    categories.id AS category_id,
                    categories.name AS category_name,
                    (SELECT COUNT(*) FROM products p WHERE p.category_id = categories.id) AS product_count
                FROM
                    products
                        LEFT JOIN
                    categories
                    ON
                        products.category_id = categories.id
                WHERE products.name = ?
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                product.name()
        ).stream().findFirst();
    }
//
    public Product addProduct(Product product) {
        var sql = """
                INSERT INTO products (name, price, category_id, stock)
                VALUES (?, ?, ?, ?)
                """;
        jdbcTemplate.update(
                sql,
                product.name(),
                product.price(),
                product.category().id(),
                product.stock()
        );

        return getProductByName(product).get();
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

    public Product updateProduct(Product product) {
        var sql = """
                UPDATE products
                SET name = ?, price = ?, category_id = ?, stock = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(
                sql,
                product.name(),
                product.price(),
                product.category().id(),
                product.stock(),
                product.id()
        );
        return getProductById(product.id()).get();
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        var sql = """
                SELECT
                    products.id AS product_id,
                    products.name AS product_name,
                    products.price AS product_price,
                    products.stock AS product_stock,
                    categories.id AS category_id,
                    categories.name AS category_name,
                    (SELECT COUNT(*) FROM products p WHERE p.category_id = categories.id) AS product_count
                FROM
                    products
                        LEFT JOIN
                    categories
                    ON
                        products.category_id = categories.id
                WHERE products.category_id = ?
                ORDER BY products.id
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                categoryId
        );
    }

    public List<Product> getProductsByProductName(String substring) {
        var sql = """
                SELECT
                    products.id AS product_id,
                    products.name AS product_name,
                    products.price AS product_price,
                    products.stock AS product_stock,
                    categories.id AS category_id,
                    categories.name AS category_name,
                    (SELECT COUNT(*) FROM products p WHERE p.category_id = categories.id) AS product_count
                FROM
                    products
                        LEFT JOIN
                    categories
                    ON
                        products.category_id = categories.id
                WHERE LOWER(products.name) LIKE LOWER(?)
                ORDER BY products.id
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                "%" + substring + "%"
        );
    }

    public List<Product> getProductsByCategoryIdAndProductName(Long categoryId, String substring) {
        var sql = """
                SELECT
                    products.id AS product_id,
                    products.name AS product_name,
                    products.price AS product_price,
                    products.stock AS product_stock,
                    categories.id AS category_id,
                    categories.name AS category_name,
                    (SELECT COUNT(*) FROM products p WHERE p.category_id = categories.id) AS product_count
                FROM
                    products
                        LEFT JOIN
                    categories
                    ON
                        products.category_id = categories.id
                WHERE products.category_id = ? AND LOWER(products.name) LIKE LOWER(?)
                ORDER BY products.id
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper(),
                categoryId,
                "%" + substring + "%"
        );
    }

//
//    public List<ProductInOrder> getProductsByOrder(Long orderId) {
//        var sql = """
//                SELECT products.id, products.name, products.price, categories.name as category_name, products_orders.amount
//                FROM products
//                JOIN products_orders ON products.id = products_orders.product_id JOIN categories ON products.category_id = categories.id
//                WHERE products_orders.order_id = ?
//                ORDER BY products.id
//                """;
//        return jdbcTemplate.query(
//                sql,
//                new ProductInOrderMapper(),
//                orderId
//        );
//    }
//
//    public List<ProductInOrder> getProductsByOrderHistory(Long orderId) {
//        var sql = """
//                SELECT products.id, products.name, products.price, categories.name as category_name, products_orders_history.amount
//                FROM products
//                JOIN products_orders_history ON products.id = products_orders_history.product_id JOIN categories ON products.category_id = categories.id
//                WHERE products_orders_history.order_id = ?
//                ORDER BY products.id
//                """;
//        return jdbcTemplate.query(
//                sql,
//                new ProductInOrderMapper(),
//                orderId
//        );
//    }
}
