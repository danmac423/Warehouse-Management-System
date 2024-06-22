package warehouse.warehousemanagementsystem.SupplyHistory;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyHistoryViewMapper implements RowMapper<SupplyHistoryView>{
    @Override
    public SupplyHistoryView mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SupplyHistoryView(
                rs.getLong("id"),
                rs.getLong("supplier_id"),
                rs.getString("supplier_name"),
                rs.getLong("worker_id"),
                rs.getString("username"),
                rs.getDate("arrival_date"),
                rs.getDate("expected_date"),
                rs.getDate("processed_date"),
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getInt("amount")
        );
    }
}
