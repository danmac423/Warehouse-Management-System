package warehouse.warehousemanagementsystem.supplier;

import org.springframework.jdbc.core.RowMapper;
import warehouse.warehousemanagementsystem.address.AddressDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierMapper implements RowMapper<SupplierDto> {
    @Override
    public SupplierDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            AddressDto address = new AddressDto(
                    rs.getLong("address_id"),
                    rs.getString("street"),
                    rs.getString("house_nr"),
                    rs.getString("postal_code"),
                    rs.getString("city"),
                    rs.getString("country")
            );

            return new SupplierDto(
                    rs.getLong("supplier_id"),
                    rs.getString("name"),
                    address
            );


    }
}
