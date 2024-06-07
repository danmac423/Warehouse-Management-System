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

    public List<Supplier> getAllSuppliers() {
        var sql = """
                SELECT * FROM suppliers
                """;
        return jdbcTemplate.query(
                sql,
                new SupplierMapper()
        );
    }

    public Optional<Supplier> getSupplierById(Long id) {
        var sql = """
                SELECT * FROM suppliers
                WHERE id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new SupplierMapper(),
                id
        ).stream().findFirst();
    }

    public Optional<Supplier> getSupplierByName(Supplier supplier) {
        var sql = """
                SELECT * FROM suppliers
                WHERE name = ?
                """;
        return jdbcTemplate.query(
                sql,
                new SupplierMapper(),
                supplier.name()
        ).stream().findFirst();
    }

    public int addSupplier(Supplier supplier) {
        var sql = """
                INSERT INTO suppliers (name, address_id)
                VALUES (?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                supplier.name(),
                supplier.addressId()
        );
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

    public int updateSupplier(Supplier supplier) {
        var sql = """
                UPDATE suppliers
                SET name = ?, address_id = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                supplier.name(),
                supplier.addressId(),
                supplier.id()
        );
    }
}
