package warehouse.warehousemanagementsystem.address;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressMapper implements RowMapper<AddressDto> {
    @Override
    public AddressDto mapRow(ResultSet resultSet, int i) throws SQLException {
        return new AddressDto(
                resultSet.getLong("id"),
                resultSet.getString("street"),
                resultSet.getString("house_nr"),
                resultSet.getString("postal_code"),
                resultSet.getString("city"),
                resultSet.getString("country")
        );
    }
}
