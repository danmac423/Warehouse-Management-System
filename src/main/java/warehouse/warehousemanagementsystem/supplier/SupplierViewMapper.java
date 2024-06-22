package warehouse.warehousemanagementsystem.supplier;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierViewMapper implements RowMapper<SupplierView> {
    @Override
    public SupplierView mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SupplierView(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getLong("address_id"),
                rs.getString("street"),
                rs.getString("house_nr"),
                rs.getString("postal_code"),
                rs.getString("city"),
                rs.getString("country")
        );
    }
}
