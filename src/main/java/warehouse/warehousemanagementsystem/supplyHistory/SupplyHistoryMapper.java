package warehouse.warehousemanagementsystem.supplyHistory;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyHistoryMapper implements RowMapper<SupplyHistoryDto>{
    @Override
    public SupplyHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SupplyHistoryDto(
                rs.getLong("id"),
                rs.getLong("supplier_id"),
                rs.getLong("worker_id"),
                rs.getDate("arrival_date"),
                rs.getDate("processed_date"),
                rs.getDate("expected_date"),
                rs.getLong("product_id"),
                rs.getInt("amount")
        );
    }
}
