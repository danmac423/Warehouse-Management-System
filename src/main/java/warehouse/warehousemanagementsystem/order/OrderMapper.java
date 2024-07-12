package warehouse.warehousemanagementsystem.order;

import org.springframework.jdbc.core.RowMapper;
import warehouse.warehousemanagementsystem.address.AddressDto;
import warehouse.warehousemanagementsystem.customer.CustomerDto;
import warehouse.warehousemanagementsystem.worker.WorkerDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<OrderDto>{


    @Override
    public OrderDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        AddressDto address = new AddressDto(
                rs.getLong("address_id"),
                rs.getString("street"),
                rs.getString("house_nr"),
                rs.getString("postal_code"),
                rs.getString("city"),
                rs.getString("country")
        );

        CustomerDto customer = new CustomerDto(
                rs.getLong("customer_id"),
                rs.getString("customer_name"),
                rs.getString("customer_last_name"),
                address,
                rs.getString("customer_email")
        );

        WorkerDto worker = new WorkerDto(
                rs.getLong("worker_id"),
                rs.getString("worker_username"),
                null,
                rs.getString("worker_name"),
                rs.getString("worker_last_name"),
                rs.getString("worker_role")
        );

        return new OrderDto(
                rs.getLong("order_id"),
                customer,
                rs.getDate("date_processed"),
                worker,
                rs.getString("status"),
                rs.getDate("date_received"),
                null,
                null
        );
    }


}
