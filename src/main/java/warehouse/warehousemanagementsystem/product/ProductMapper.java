package warehouse.warehousemanagementsystem.product;

import org.springframework.jdbc.core.RowMapper;
import warehouse.warehousemanagementsystem.category.CategoryDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapper implements RowMapper<ProductDto> {
    @Override
    public ProductDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryDto category = new CategoryDto(
                rs.getLong("category_id"),
                rs.getString("category_name"),
                rs.getInt("product_count")
        );

        return new ProductDto(
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getBigDecimal("product_price"),
                category,
                rs.getInt("product_stock")
        );
    }
}
