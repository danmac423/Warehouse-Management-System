package warehouse.warehousemanagementsystem.supply;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyMapper implements RowMapper<Supply>{
    @Override
    public Supply mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Supply(
                rs.getLong("id"),
                rs.getLong("supplier_id"),
                rs.getLong("worker_id"),
                rs.getString("state"),
                rs.getDate("arrival_date"),
                rs.getDate("processed_date"),
                rs.getDate("expected_date"),
                rs.getLong("product_id"),
                rs.getInt("amount")
        );
    }
}
