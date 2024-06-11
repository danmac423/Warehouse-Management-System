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
                SELECT * FROM supplies_history
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryMapper()
        );
    }

    public List<SupplyHistory> getSupplyByWorker(Long workerId) {
        var sql = """
                SELECT * FROM supplies_history
                WHERE worker_id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryMapper(),
                workerId
        );
    }

    public List<SupplyHistory> getSupplyByProduct(Long productId) {
        var sql = """
                SELECT * FROM supplies_history
                WHERE product_id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryMapper(),
                productId
        );
    }

    public List<SupplyHistory> getSupplyBySupplier(Long supplierId) {
        var sql = """
                SELECT * FROM supplies_history
                WHERE supplier_id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryMapper(),
                supplierId
        );
    }
}
