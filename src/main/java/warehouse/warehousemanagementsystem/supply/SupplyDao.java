package warehouse.warehousemanagementsystem.supply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SupplyDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SupplyDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Supply> getAllSupplies() {
        var sql = """
                SELECT * FROM supplies
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyMapper()
        );
    }

    public int addSupply(Supply supply) {
        var sql = """
                INSERT INTO supplies (supplier_id, worker_id, status, arrival_date, processed_date, expected_date, product_it, amount)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                supply.supplierId(),
                supply.workerId(),
                supply.status(),
                supply.arrivalDate(),
                supply.processedDate(),
                supply.expectedDate(),
                supply.productId(),
                supply.amount()
        );
    }

    public int updateSupply(Supply supply) {
        var sql = """
                UPDATE supplies
                SET supplier_id = ?, worker_id = ?, status = ?, arrival_date = ?, processed_date = ?
                expected_date = ?, expected_date = ?, product_id = ?, amount = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                supply.supplierId(),
                supply.workerId(),
                supply.status(),
                supply.arrivalDate(),
                supply.processedDate(),
                supply.expectedDate(),
                supply.productId(),
                supply.amount()
        );
    }

}
