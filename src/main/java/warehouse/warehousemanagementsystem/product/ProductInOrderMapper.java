package warehouse.warehousemanagementsystem.product;

import org.springframework.jdbc.core.RowMapper;
import warehouse.warehousemanagementsystem.category.Category;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductInOrderMapper  implements RowMapper<ProductInOrder> {
    @Override
    public ProductInOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
        Category category = new Category(
                rs.getLong("category_id"),
                rs.getString("category_name"),
                rs.getInt("product_count")
        );

        return new ProductInOrder(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getBigDecimal("price"),
                category,
                rs.getInt("amount"),
                rs.getBigDecimal("price").multiply(new BigDecimal(rs.getInt("amount")))
        );
    }

}
