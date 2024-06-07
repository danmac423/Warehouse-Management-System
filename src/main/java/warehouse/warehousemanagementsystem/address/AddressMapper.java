package warehouse.warehousemanagementsystem.address;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressMapper implements RowMapper<Address> {
    @Override
    public Address mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Address(
                resultSet.getLong("id"),
                resultSet.getString("street"),
                resultSet.getInt("house_nr"),
                resultSet.getString("postal_code"),
                resultSet.getString("city"),
                resultSet.getString("country")
        );
    }
}
