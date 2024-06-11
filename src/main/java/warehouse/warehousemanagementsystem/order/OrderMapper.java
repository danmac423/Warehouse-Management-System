package warehouse.warehousemanagementsystem.order;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order>{
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Order(
                rs.getLong("id"),
                rs.getLong("customer_id"),
                rs.getDate("date_processed"),
                rs.getLong("worker_id"),
                rs.getString("status"),
                rs.getDate("date_received"),
                rs.getBigDecimal("total_price")
        );
    }
}
