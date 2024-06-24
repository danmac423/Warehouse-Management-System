package warehouse.warehousemanagementsystem.supply;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyViewMapper implements RowMapper<SupplyView>{
    @Override
    public SupplyView mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SupplyView(
                rs.getLong("id"),
                rs.getLong("supplier_id"),
                rs.getString("supplier_name"),
                rs.getLong("worker_id"),
                rs.getString("username"),
                rs.getString("status"),
                rs.getDate("arrival_date"),
                rs.getDate("expected_date"),
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getInt("amount")
        );
    }
}
