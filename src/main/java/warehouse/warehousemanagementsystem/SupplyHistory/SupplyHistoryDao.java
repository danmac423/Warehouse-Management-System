package warehouse.warehousemanagementsystem.SupplyHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.warehousemanagementsystem.supply.SupplyView;
import warehouse.warehousemanagementsystem.supply.SupplyViewMapper;

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
                ORDER BY id
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
                WHERE LOWER(p.name) LIKE LOWER(?);
                """;
        String toSearch = "%" +  productName + "%";
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryMapper(),
                toSearch
        );
    }

    public List<SupplyHistory> getSupplyBySupplier(String supplierName) {
        var sql = """
                SELECT s.id, s.supplier_id, s.worker_id, s.arrival_date, s.processed_date, s.expected_date, s.product_id, s.amount
                FROM supplies_history s
                JOIN suppliers p ON s.supplier_id = p.id
                WHERE LOWER(p.name) LIKE LOWER(?);
                """;
        String toSearch = "%" +  supplierName + "%";
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryMapper(),
                toSearch
        );
    }
    public List<SupplyHistoryView> getAllSuppliesHistViews() {
        var sql = """
                SELECT
                    supplies_history.id,
                    supplier.id AS supplier_id,
                    supplier.name AS supplier_name,
                    worker.id AS worker_id,
                    worker.username,
                    supplies_history.arrival_date,
                    supplies_history.expected_date,
					supplies_history.processed_date,
                    supplies_history.product_id,
                    product.name AS product_name,
                    supplies_history.amount
                FROM
                    supplies_history
                LEFT JOIN
                    workers worker ON worker.id = supplies_history.worker_id
                LEFT JOIN
                    products product ON supplies_history.product_id = product.id
                LEFT JOIN
                    suppliers supplier ON supplies_history.supplier_id = supplier.id
                GROUP BY
                    supplies_history.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies_history.arrival_date, supplies_history.expected_date,
					supplies_history, supplies_history.product_id, product.name, supplies_history.amount
                ORDER BY
                    supplies_history.id;""";
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryViewMapper()
        );
    }

    public List<SupplyHistoryView> getAllSuppliesHistViewsByWorkerUsername(String username) {
        var sql = """
                SELECT
                    supplies_history.id,
                    supplier.id AS supplier_id,
                    supplier.name AS supplier_name,
                    worker.id AS worker_id,
                    worker.username,
                    supplies_history.arrival_date,
                    supplies_history.expected_date,
					supplies_history.processed_date,
                    supplies_history.product_id,
                    product.name AS product_name,
                    supplies_history.amount
                FROM
                    supplies_history
                LEFT JOIN
                    workers worker ON worker.id = supplies_history.worker_id
                LEFT JOIN
                    products product ON supplies_history.product_id = product.id
                LEFT JOIN
                    suppliers supplier ON supplies_history.supplier_id = supplier.id
                WHERE worker.id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))
                GROUP BY
                    supplies_history.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies_history.arrival_date, supplies_history.expected_date,
					supplies_history, supplies_history.product_id, product.name, supplies_history.amount
                ORDER BY
                    supplies_history.id;""";
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryViewMapper(),
                "%" + username + "%"
        );
    }

    public List<SupplyHistoryView> getAllSuppliesViewsHistBySupplierName(String name) {
        var sql = """
                SELECT
                    supplies_history.id,
                    supplier.id AS supplier_id,
                    supplier.name AS supplier_name,
                    worker.id AS worker_id,
                    worker.username,
                    supplies_history.arrival_date,
                    supplies_history.expected_date,
					supplies_history.processed_date,
                    supplies_history.product_id,
                    product.name AS product_name,
                    supplies_history.amount
                FROM
                    supplies_history
                LEFT JOIN
                    workers worker ON worker.id = supplies_history.worker_id
                LEFT JOIN
                    products product ON supplies_history.product_id = product.id
                LEFT JOIN
                    suppliers supplier ON supplies_history.supplier_id = supplier.id
                WHERE supplier.id IN (SELECT id FROM suppliers WHERE LOWER(suppliers.name) like LOWER((?)))
                GROUP BY
                    supplies_history.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies_history.arrival_date, supplies_history.expected_date,
					supplies_history, supplies_history.product_id, product.name, supplies_history.amount
                ORDER BY
                    supplies_history.id;""";
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryViewMapper(),
                "%" + name + "%"
        );
    }

    public List<SupplyHistoryView> getSuppliesHistViewsBySupplierNameWorkerUsername(String name, String username) {
        var sql = """
                SELECT
                    supplies_history.id,
                    supplier.id AS supplier_id,
                    supplier.name AS supplier_name,
                    worker.id AS worker_id,
                    worker.username,
                    supplies_history.arrival_date,
                    supplies_history.expected_date,
					supplies_history.processed_date,
                    supplies_history.product_id,
                    product.name AS product_name,
                    supplies_history.amount
                FROM
                    supplies_history
                LEFT JOIN
                    workers worker ON worker.id = supplies_history.worker_id
                LEFT JOIN
                    products product ON supplies_history.product_id = product.id
                LEFT JOIN
                    suppliers supplier ON supplies_history.supplier_id = supplier.id
                WHERE supplier.id IN (SELECT id FROM suppliers WHERE LOWER(suppliers.name) like LOWER((?))) AND
                    worker.id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))
                GROUP BY
                    supplies_history.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies_history.arrival_date, supplies_history.expected_date,
					supplies_history, supplies_history.product_id, product.name, supplies_history.amount
                ORDER BY
                    supplies_history.id;""";
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryViewMapper(),
                "%" + name + "%",
                "%" + username + "%"
        );
    }

}
