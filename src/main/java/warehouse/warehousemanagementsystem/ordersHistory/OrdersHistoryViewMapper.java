package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdersHistoryViewMapper implements RowMapper<OrdersHistoryView> {
    @Override
    public OrdersHistoryView mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrdersHistoryView(
                rs.getLong("id"),
                rs.getLong("customer_id"),
                rs.getString("customer_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getDate("date_processed"),
                rs.getLong("worker_id"),
                rs.getString("username"),
                rs.getDate("date_received"),
                rs.getBigDecimal("total_price")
        );
    }
}
