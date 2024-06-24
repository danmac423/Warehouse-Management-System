package warehouse.warehousemanagementsystem.supply;

import org.springframework.jdbc.core.RowMapper;
import warehouse.warehousemanagementsystem.address.Address;
import warehouse.warehousemanagementsystem.category.Category;
import warehouse.warehousemanagementsystem.product.Product;
import warehouse.warehousemanagementsystem.supplier.Supplier;
import warehouse.warehousemanagementsystem.worker.Worker;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplyMapper implements RowMapper<Supply>{
    @Override
    public Supply mapRow(ResultSet rs, int rowNum) throws SQLException {
        Address address = new Address(
                rs.getLong("address_id"),
                rs.getString("street"),
                rs.getString("house_nr"),
                rs.getString("postal_code"),
                rs.getString("city"),
                rs.getString("country")
        );

        Supplier supplier = new Supplier(
                rs.getLong("supplier_id"),
                rs.getString("supplier_name"),
                address
        );

        Worker worker;
        if (rs.getInt("worker_id") != 0) {
            worker = new Worker(
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

        Category category = new Category(
                rs.getLong("category_id"),
                rs.getString("category_name"),
                rs.getInt("product_count")
        );

        Product product = new Product(
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getBigDecimal("price"),
                category,
                rs.getInt("stock")
        );

        return new Supply(
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
