package warehouse.warehousemanagementsystem.category;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryMapper implements RowMapper<CategoryDto> {
    @Override
    public CategoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CategoryDto(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("product_count")
        );
    }
}
