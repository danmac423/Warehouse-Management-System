package warehouse.warehousemanagementsystem.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SupplierDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SupplierDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Supplier> getSuppliers(String supplierName, String country, String city) {
        if (supplierName == null) {
            supplierName = "";
        }
        if (country == null) {
            country = "";
        }
        if (city == null) {
            city = "";
        }
        var sql = """
                    SELECT suppliers.id AS supplier_id, suppliers.name, addresses.id AS address_id, addresses.street, addresses.house_nr, addresses.postal_code, addresses.city, addresses.country
                    FROM suppliers LEFT JOIN addresses ON addresses.id = suppliers.address_id
                    WHERE
                       LOWER(suppliers.name) LIKE LOWER(?) AND
                       LOWER(addresses.country) LIKE LOWER(?) AND
                       LOWER(addresses.city) LIKE LOWER(?)
                    ORDER BY suppliers.id
              """;
        return jdbcTemplate.query(
                sql,
                new SupplierMapper()
                , "%" + supplierName + "%"
                , "%" + country + "%"
                , "%" + city + "%"
        );
    }

    public Optional<Supplier> getSupplierById(Long id) {
        var sql = """
                SELECT suppliers.id AS supplier_id, suppliers.name, addresses.id AS address_id, addresses.street, addresses.house_nr, addresses.postal_code, addresses.city, addresses.country
                FROM suppliers LEFT JOIN addresses ON addresses.id = suppliers.address_id
                WHERE suppliers.id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new SupplierMapper(),
                id
        ).stream().findFirst();
    }

    public Optional<Supplier> getSupplierByName(String name) {
        var sql = """
                SELECT suppliers.id AS supplier_id, suppliers.name, addresses.id AS address_id, addresses.street, addresses.house_nr, addresses.postal_code, addresses.city, addresses.country
                FROM suppliers LEFT JOIN addresses ON addresses.id = suppliers.address_id
                WHERE name = ?
                """;
        return jdbcTemplate.query(
                sql,
                new SupplierMapper(),
                name
        ).stream().findFirst();
    }

    public Supplier addSupplier(Supplier supplier) {
        var sql = """
                INSERT INTO suppliers (name, address_id)
                VALUES (?, ?)
                """;
        jdbcTemplate.update(
                sql,
                supplier.name(),
                supplier.address().id()
        );
        return getSupplierByName(supplier.name()).orElseThrow();
    }

    public int deleteSupplier(Long id) {
        var sql = """
                DELETE FROM suppliers
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                id
        );
    }


    public Supplier updateSupplier(Supplier supplier) {
        var sql = """
                UPDATE suppliers
                SET name = ?, address_id = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(
                sql,
                supplier.name(),
                supplier.address().id(),
                supplier.id()
        );
        return getSupplierById(supplier.id()).orElseThrow();
    }

}
