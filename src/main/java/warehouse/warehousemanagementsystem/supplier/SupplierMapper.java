package warehouse.warehousemanagementsystem.supplier;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierMapper implements RowMapper<Supplier> {
    @Override
    public Supplier mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Supplier(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getLong("address_id")
        );
    }
}
