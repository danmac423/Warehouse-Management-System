package warehouse.warehousemanagementsystem.orderView;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderViewMapper implements RowMapper<OrderView>{
    @Override
    public OrderView mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrderView(
                rs.getLong("id"),
                rs.getLong("customer_id"),
                rs.getString("customer_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getDate("date_processed"),
                rs.getLong("worker_id"),
                rs.getString("username"),
                rs.getString("status"),
                rs.getDate("date_received"),
                rs.getBigDecimal("total_price")
        );
    }
}
