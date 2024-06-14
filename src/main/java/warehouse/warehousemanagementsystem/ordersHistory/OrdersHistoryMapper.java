package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdersHistoryMapper implements RowMapper<OrdersHistory> {
    @Override
    public OrdersHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrdersHistory(
                rs.getLong("id"),
                rs.getLong("customer_id"),
                rs.getTimestamp("date_processed"),
                rs.getLong("worker_id"),
                rs.getTimestamp("date_received")
        );
    }
}
