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
                ORDER BY id
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyMapper()
        );
    }

    public int addSupply(Supply supply) {
        var sql = """
                INSERT INTO supplies (supplier_id, worker_id, status, arrival_date, processed_date, expected_date, product_id, amount)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                supply.supplierId(),
                supply.workerId(),
                "underway",
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
                SET supplier_id = ?, worker_id = ?, status = ?, arrival_date = ?, processed_date = ?,
                expected_date = ?, product_id = ?, amount = ?
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
                supply.amount(),
                supply.id()
        );
    }

    public List<Supply> getSupplyByWorker(Long workerId) {
        var sql = """
                SELECT * FROM supplies
                WHERE worker_id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyMapper(),
                workerId
        );
    }

    public List<Supply> getSupplyByProduct(Long productId) {
        var sql = """
                SELECT * FROM supplies
                WHERE product_id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyMapper(),
                productId
        );
    }

    public List<Supply> getSupplyBySupplier(Long supplierId) {
        var sql = """
                SELECT * FROM supplies
                WHERE supplier_id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyMapper(),
                supplierId
        );
    }

    public int acknowledgeSupply(Supply supply) {
        var sql = "UPDATE supplies SET status = ? WHERE id = ?";
        return jdbcTemplate.update(
                sql,
                "arrived",
                supply.id()
        );
    }

    public int unpackSupply(Supply supply) {
        var sql = "UPDATE supplies SET status = ? WHERE id = ?";
        return jdbcTemplate.update(
                sql,
                "processed",
                supply.id()
        );
    }

    public int updateWorker(Supply supply) {
        var sql = """
                UPDATE supplies
                SET worker_id = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                supply.workerId(),
                supply.id()
        );
    }

    public List<SupplyView> getAllSuppliesViews() {
        var sql = """
                SELECT
                    supplies.id,
                    supplier.id AS supplier_id,
                    supplier.name AS supplier_name,
                    worker.id AS worker_id,
                    worker.username,
                    supplies.status,
                    supplies.arrival_date,
                    supplies.expected_date,
                    supplies.product_id,
                    product.name AS product_name,
                    supplies.amount
                FROM
                    supplies
                LEFT JOIN
                    workers worker ON worker.id = supplies.worker_id
                LEFT JOIN
                    products product ON supplies.product_id = product.id
                LEFT JOIN
                    suppliers supplier ON supplies.supplier_id = supplier.id
                GROUP BY
                    supplies.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies.status, supplies.arrival_date, supplies.expected_date, supplies.product_id, product.name, supplies.amount
                ORDER BY
                    supplies.id;""";
        return jdbcTemplate.query(
                sql,
                new SupplyViewMapper()
        );
    }

    public List<SupplyView> getAllSuppliesViewsByWorkerUsername(String usernameSubstring) {
        var sql = """
                SELECT
                    supplies.id,
                    supplier.id AS supplier_id,
                    supplier.name AS supplier_name,
                    worker.id AS worker_id,
                    worker.username,
                    supplies.status,
                    supplies.arrival_date,
                    supplies.expected_date,
                    supplies.product_id,
                    product.name AS product_name,
                    supplies.amount
                FROM
                    supplies
                LEFT JOIN
                    workers worker ON worker.id = supplies.worker_id
                LEFT JOIN
                    products product ON supplies.product_id = product.id
                LEFT JOIN
                    suppliers supplier ON supplies.supplier_id = supplier.id
                WHERE worker.id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))
                GROUP BY
                    supplies.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies.status, supplies.arrival_date, supplies.expected_date, supplies.product_id, product.name, supplies.amount
                ORDER BY
                    supplies.id;""";
        return jdbcTemplate.query(
                sql,
                new SupplyViewMapper(),
                "%" + usernameSubstring + "%"
        );
    }

    public List<SupplyView> getAllSuppliesViewsBySupplierName(String name) {
        var sql = """
                SELECT
                    supplies.id,
                    supplier.id AS supplier_id,
                    supplier.name AS supplier_name,
                    worker.id AS worker_id,
                    worker.username,
                    supplies.status,
                    supplies.arrival_date,
                    supplies.expected_date,
                    supplies.product_id,
                    product.name AS product_name,
                    supplies.amount
                FROM
                    supplies
                LEFT JOIN
                    workers worker ON worker.id = supplies.worker_id
                LEFT JOIN
                    products product ON supplies.product_id = product.id
                LEFT JOIN
                    suppliers supplier ON supplies.supplier_id = supplier.id
                WHERE supplier.id IN (SELECT id FROM suppliers WHERE LOWER(suppliers.name) like LOWER((?)))
                GROUP BY
                    supplies.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies.status, supplies.arrival_date, supplies.expected_date, supplies.product_id, product.name, supplies.amount
                ORDER BY
                    supplies.id;""";
        return jdbcTemplate.query(
                sql,
                new SupplyViewMapper(),
                "%" + name + "%"
        );
    }

    public List<SupplyView> getAllSuppliesViewsBySupplierNameAndUsername(String name, String username) {
        var sql = """
                SELECT
                    supplies.id,
                    supplier.id AS supplier_id,
                    supplier.name AS supplier_name,
                    worker.id AS worker_id,
                    worker.username,
                    supplies.status,
                    supplies.arrival_date,
                    supplies.expected_date,
                    supplies.product_id,
                    product.name AS product_name,
                    supplies.amount
                FROM
                    supplies
                LEFT JOIN
                    workers worker ON worker.id = supplies.worker_id
                LEFT JOIN
                    products product ON supplies.product_id = product.id
                LEFT JOIN
                    suppliers supplier ON supplies.supplier_id = supplier.id
                WHERE supplier.id IN (SELECT id FROM suppliers WHERE LOWER(suppliers.name) like LOWER((?)))
                    worker.id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))
                GROUP BY
                    supplies.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies.status, supplies.arrival_date, supplies.expected_date, supplies.product_id, product.name, supplies.amount
                ORDER BY
                    supplies.id;""";
        return jdbcTemplate.query(
                sql,
                new SupplyViewMapper(),
                "%" + name + "%",
                "%" + username + "%"
        );
    }

    public List<SupplyView> getSuppliesViewsByStatus(String status) {
        var sql = """
                SELECT
                    supplies.id,
                    supplier.id AS supplier_id,
                    supplier.name AS supplier_name,
                    worker.id AS worker_id,
                    worker.username,
                    supplies.status,
                    supplies.arrival_date,
                    supplies.expected_date,
                    supplies.product_id,
                    product.name AS product_name,
                    supplies.amount
                FROM
                    supplies
                LEFT JOIN
                    workers worker ON worker.id = supplies.worker_id
                LEFT JOIN
                    products product ON supplies.product_id = product.id
                LEFT JOIN
                    suppliers supplier ON supplies.supplier_id = supplier.id
                WHERE supplies.status = ?
                GROUP BY
                    supplies.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies.status, supplies.arrival_date, supplies.expected_date, supplies.product_id, product.name, supplies.amount
                ORDER BY
                    supplies.id;""";
        return jdbcTemplate.query(
                sql,
                new SupplyViewMapper(),
                status
        );
    }
}