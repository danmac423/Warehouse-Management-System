package warehouse.warehousemanagementsystem.product;

import org.springframework.jdbc.core.RowMapper;
import warehouse.warehousemanagementsystem.category.CategoryDto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductInOrderMapper  implements RowMapper<ProductInOrderDto> {
    @Override
    public ProductInOrderDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryDto category = new CategoryDto(
                rs.getLong("category_id"),
                rs.getString("category_name"),
                null
        );

        return new ProductInOrderDto(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getBigDecimal("price"),
                category,
                rs.getInt("amount"),
                rs.getBigDecimal("price").multiply(new BigDecimal(rs.getInt("amount")))
        );
    }

}
