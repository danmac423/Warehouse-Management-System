package warehouse.warehousemanagementsystem.supplier;

import org.springframework.jdbc.core.RowMapper;
import warehouse.warehousemanagementsystem.address.Address;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierMapper implements RowMapper<Supplier> {
    @Override
    public Supplier mapRow(ResultSet rs, int rowNum) throws SQLException {
            Address address = new Address(
                    rs.getLong("address_id"),
                    rs.getString("street"),
                    rs.getString("house_nr"),
                    rs.getString("postal_code"),
                    rs.getString("city"),
                    rs.getString("country")
            );

            return new Supplier(
                    rs.getLong("supplier_id"),
                    rs.getString("name"),
                    address
            );


    }
}
