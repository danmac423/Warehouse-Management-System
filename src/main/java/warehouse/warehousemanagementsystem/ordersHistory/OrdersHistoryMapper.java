package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdersHistoryMapper implements RowMapper<OrdersHistoryDto> {
    @Override
    public OrdersHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrdersHistoryDto(
                rs.getLong("id"),
                rs.getLong("customer_id"),
                rs.getTimestamp("date_processed"),
                rs.getLong("worker_id"),
                rs.getTimestamp("date_received")
        );
    }
}
