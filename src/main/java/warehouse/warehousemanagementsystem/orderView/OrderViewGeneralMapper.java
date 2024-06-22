package warehouse.warehousemanagementsystem.orderView;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderViewGeneralMapper implements RowMapper<OrderViewGeneral>{
    @Override
    public OrderViewGeneral mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrderViewGeneral(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getDate("date_processed"),
                rs.getString("username"),
                rs.getString("status"),
                rs.getDate("date_received"),
                rs.getBigDecimal("total_price")
        );
    }
}
