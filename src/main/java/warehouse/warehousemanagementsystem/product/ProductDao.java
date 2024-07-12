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

    public List<ProductDto> getProducts(String productName, Long categoryId) {
        if (productName == null) {
            productName = "";
        }
        if (categoryId == null) {
            categoryId = 0L;
        }
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
                WHERE LOWER(products.name) LIKE LOWER(?) AND
                        (products.category_id = ? OR ? = 0)
                ORDER BY
                products.id;
                """;
        return jdbcTemplate.query(
                sql,
                new ProductMapper()
                , "%" + productName + "%"
                , categoryId
                , categoryId
        );
    }

    public List<ProductInOrderDto> getProductsByOrderHistory(Long orderHistoryId) {
       var sql = """
               SELECT products.id, products.name, products.price,
                       categories.id as category_id, categories.name as category_name,
                       products_orders_history.amount
                FROM products
                LEFT JOIN products_orders_history ON products.id = products_orders_history.product_id
                LEFT JOIN categories ON products.category_id = categories.id
                WHERE products_orders_history.order_id = ?
                ORDER BY products.id
               """;
       return jdbcTemplate.query(
               sql,
               new ProductInOrderMapper(),
               orderHistoryId
       );
    }

    public Optional<ProductDto> getProductById(Long id) {
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

    public Optional<ProductDto> getProductByName(String name) {
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
                name
        ).stream().findFirst();
    }

    public ProductDto addProduct(ProductDto product) {
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

        return getProductByName(product.name()).get();
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

    public ProductDto updateProduct(ProductDto product) {
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

    public List<ProductInOrderDto> getProductsInOrder(Long orderId) {
        var sql = """
                SELECT products.id, products.name, products.price,
                       categories.id as category_id, categories.name as category_name,
                       products_orders.amount
                FROM products
                LEFT JOIN products_orders ON products.id = products_orders.product_id
                LEFT JOIN categories ON products.category_id = categories.id
                WHERE products_orders.order_id = ?
                ORDER BY products.id
                """;
        return jdbcTemplate.query(
                sql,
                new ProductInOrderMapper(),
                orderId
        );
    }

}
