package warehouse.warehousemanagementsystem.supply;

import org.springframework.jdbc.core.RowMapper;
import warehouse.warehousemanagementsystem.address.AddressDto;
import warehouse.warehousemanagementsystem.category.CategoryDto;
import warehouse.warehousemanagementsystem.product.ProductDto;
import warehouse.warehousemanagementsystem.supplier.SupplierDto;
import warehouse.warehousemanagementsystem.worker.WorkerDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyMapper implements RowMapper<SupplyDto>{
    @Override
    public SupplyDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        AddressDto address = new AddressDto(
                rs.getLong("address_id"),
                rs.getString("street"),
                rs.getString("house_nr"),
                rs.getString("postal_code"),
                rs.getString("city"),
                rs.getString("country")
        );

        SupplierDto supplier = new SupplierDto(
                rs.getLong("supplier_id"),
                rs.getString("supplier_name"),
                address
        );

        WorkerDto worker;
        if (rs.getInt("worker_id") != 0) {
            worker = new WorkerDto(
                    rs.getLong("worker_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("worker_name"),
                    rs.getString("worker_last_name"),
                    rs.getString("role")
            );
        } else {
            worker = null;
        }

        CategoryDto category = new CategoryDto(
                rs.getLong("category_id"),
                rs.getString("category_name"),
                rs.getInt("product_count")
        );

        ProductDto product = new ProductDto(
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getBigDecimal("price"),
                category,
                rs.getInt("stock")
        );

        return new SupplyDto(
                rs.getLong("supply_id"),
                supplier,
                worker,
                rs.getString("status"),
                rs.getDate("arrival_date"),
                rs.getDate("processed_date"),
                rs.getDate("expected_date"),
                product,
                rs.getInt("amount")
        );
    }
}
