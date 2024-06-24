package warehouse.warehousemanagementsystem.product;

import org.springframework.jdbc.core.RowMapper;
import warehouse.warehousemanagementsystem.category.Category;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Category category = new Category(
                rs.getLong("category_id"),
                rs.getString("category_name"),
                rs.getInt("product_count")
        );

        return new Product(
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getBigDecimal("product_price"),
                category,
                rs.getInt("product_stock")
        );
    }
}
