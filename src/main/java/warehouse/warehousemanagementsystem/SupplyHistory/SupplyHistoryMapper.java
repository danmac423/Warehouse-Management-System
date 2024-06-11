package warehouse.warehousemanagementsystem.SupplyHistory;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyHistoryMapper implements RowMapper<SupplyHistory>{
    @Override
    public SupplyHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SupplyHistory(
                rs.getLong("id"),
                rs.getLong("supplier_id"),
                rs.getLong("worker_id"),
                rs.getString("status"),
                rs.getDate("arrival_date"),
                rs.getDate("processed_date"),
                rs.getDate("expected_date"),
                rs.getLong("product_id"),
                rs.getInt("amount")
        );
    }
}
