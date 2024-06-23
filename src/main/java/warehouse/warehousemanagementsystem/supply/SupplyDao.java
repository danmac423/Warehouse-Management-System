package warehouse.warehousemanagementsystem.supply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SupplyDao {
    private final JdbcTemplate jdbcTemplate;
    private final String  sqlPreffix;
    private final String sqlSuffix;

    @Autowired
    public SupplyDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        sqlPreffix = """
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
                """;
        sqlSuffix = """
                GROUP BY
                    supplies.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies.status, supplies.arrival_date, supplies.expected_date, supplies.product_id, product.name, supplies.amount
                ORDER BY
                    supplies.id;""";
    }

    public List<Supply> getAllSupplies() {
        var sql = """
                SELECT addresses.id AS address_id, street, house_nr, postal_code, city, country,
                    suppliers.id AS supplier_id, suppliers.name AS supplier_name,
                    workers.id AS worker_id, workers.username, workers.name AS worker_name, workers.last_name AS worker_last_name,
                FROM supplies 
                    LEFT JOIN suppliers ON supplies.supplier_id = suppliers.id
                    LEFT JOIN addresses ON suppliers.address_id = addresses.id
                    LEFT JOIN workers ON supplies.worker_id = workers.id
                    LEFT JOIN products ON supplies.product_id = products.id
                
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
        var sql = sqlPreffix.concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new SupplyViewMapper()
        );
    }

    public List<SupplyView> getAllSuppliesViewsByWorkerUsername(String usernameSubstring) {
        var sql = sqlPreffix.concat("""
                WHERE worker.id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))""")
                .concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new SupplyViewMapper(),
                "%" + usernameSubstring + "%"
        );
    }

    public List<SupplyView> getAllSuppliesViewsBySupplierName(String name) {
        var sql = sqlPreffix.concat("""
                WHERE supplier.id IN (SELECT id FROM suppliers WHERE LOWER(suppliers.name) like LOWER((?)))
                """).concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new SupplyViewMapper(),
                "%" + name + "%"
        );
    }

    public List<SupplyView> getAllSuppliesViewsBySupplierNameAndUsername(String name, String username) {
        var sql = sqlPreffix.concat("""
                WHERE supplier.id IN (SELECT id FROM suppliers WHERE LOWER(suppliers.name) like LOWER((?))) AND
                    worker.id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))
                """).
                concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new SupplyViewMapper(),
                "%" + name + "%",
                "%" + username + "%"
        );
    }

    public List<SupplyView> getSuppliesViewsByStatus(String status) {
        var sql = sqlPreffix.concat("""
                WHERE supplies.status = ?
                """)
                .concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new SupplyViewMapper(),
                status
        );
    }

    public List<SupplyView> getSuppliesViewsByWorkerId(Long workerId) {
        var sql = sqlPreffix.concat("""
                WHERE supplies.worker_id = ?
                """).
                concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new SupplyViewMapper(),
                workerId
        );
    }
}