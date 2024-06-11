package warehouse.warehousemanagementsystem.SupplyHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SupplyHistoryDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SupplyHistoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SupplyHistory> getAllSupplies() {
        var sql = """
                SELECT * FROM supplies_history;
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryMapper()
        );
    }

    public List<SupplyHistory> getSupplyByWorker(Long workerId) {
        var sql = """
                SELECT * FROM supplies_history
                WHERE worker_id = ?;
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryMapper(),
                workerId
        );
    }

    public List<SupplyHistory> getSupplyByProduct(String productName) {
        var sql = """
                SELECT s.id, s.supplier_id, s.worker_id, s.arrival_date, s.processed_date, s.expected_date, s.product_id, s.amount
                FROM supplies_history s
                JOIN products p ON s.product_id = p.id
                WHERE p.name LIKE '%?%';
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryMapper(),
                productName
        );
    }

    public List<SupplyHistory> getSupplyBySupplier(String supplierName) {
        var sql = """
                SELECT s.id, s.supplier_id, s.worker_id, s.arrival_date, s.processed_date, s.expected_date, s.product_id, s.amount
                FROM supplies_history s
                JOIN suppliers p ON s.supplier_id = p.id
                WHERE p.name LIKE '%?%';
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryMapper(),
                supplierName
        );
    }
}
