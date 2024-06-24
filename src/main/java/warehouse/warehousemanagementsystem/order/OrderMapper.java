package warehouse.warehousemanagementsystem.order;

import org.springframework.jdbc.core.RowMapper;
import warehouse.warehousemanagementsystem.address.Address;
import warehouse.warehousemanagementsystem.customer.Customer;
import warehouse.warehousemanagementsystem.worker.Worker;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order>{


    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Address address = new Address(
                rs.getLong("address_id"),
                rs.getString("street"),
                rs.getString("house_nr"),
                rs.getString("postal_code"),
                rs.getString("city"),
                rs.getString("country")
        );

        Customer customer = new Customer(
                rs.getLong("customer_id"),
                rs.getString("customer_name"),
                rs.getString("customer_last_name"),
                address,
                rs.getString("customer_email")
        );

        Worker worker = new Worker(
                rs.getLong("worker_id"),
                rs.getString("worker_username"),
                null,
                rs.getString("worker_name"),
                rs.getString("worker_last_name"),
                rs.getString("worker_role")
        );

        return new Order(
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
