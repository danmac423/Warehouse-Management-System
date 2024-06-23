package warehouse.warehousemanagementsystem.supply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SupplyDao {
    private final JdbcTemplate jdbcTemplate;
    private final String  sqlPreffix;
    private final String sqlSuffix;

    @Autowired
    public SupplyDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        sqlPreffix = """
                SELECT addresses.id AS address_id, street, house_nr, postal_code, city, country,
                    suppliers.id AS supplier_id, suppliers.name AS supplier_name,
                    workers.id AS worker_id, workers.username, workers.password, workers.name AS worker_name, workers.last_name AS worker_last_name, workers.role,
                    products.id AS product_id, products.name AS product_name, products.price, products.stock,
                    categories.id AS category_id, categories.name AS category_name, (SELECT COUNT(*) FROM products p WHERE p.category_id = categories.id) AS product_count,
                    supplies.id AS supply_id, supplies.status, supplies.arrival_date, supplies.processed_date, supplies.expected_date, supplies.amount
                FROM supplies
                    LEFT JOIN suppliers ON supplies.supplier_id = suppliers.id
                    LEFT JOIN addresses ON suppliers.address_id = addresses.id
                    LEFT JOIN workers ON supplies.worker_id = workers.id
                    LEFT JOIN products ON supplies.product_id = products.id
                    LEFT JOIN categories ON products.category_id = categories.id
                """;
        sqlSuffix = """
                GROUP BY
                    supplies.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies.status, supplies.arrival_date, supplies.expected_date, supplies.product_id, product.name, supplies.amount
                ORDER BY
                    supplies.id;""";
    }



    public List<Supply> getSupplies(String supplierName, String workerUsername, String productName, String status, Long workerId) {
        if (supplierName == null) {
            supplierName = "";
        }
        if (workerUsername == null) {
            workerUsername = "";
        }
        if (productName == null) {
            productName = "";
        }
        if (status == null) {
            status = "";
        }
        if (workerId == null) {
            workerId = 0L;
        }

        var sql = sqlPreffix.concat("""
                WHERE LOWER(suppliers.name) LIKE LOWER(?) AND
                      (LOWER(workers.username) LIKE LOWER(?) OR (worker_id ISNULL AND ? = '' )) AND
                      LOWER(products.name) LIKE LOWER(?) AND
                      LOWER(supplies.status) LIKE LOWER(?) AND
                      (? = workers.id OR (? = 0))
                      ORDER BY supplies.id
                """);
        return jdbcTemplate.query(
                sql,
                new SupplyMapper(),
                "%" + supplierName + "%",
                "%" + workerUsername + "%",
                workerUsername,
                "%" + productName + "%",
                "%" + status + "%",
                workerId,
                workerId
        );
    }

    Optional<Supply> getSupplyById(Long id) {
        var sql = sqlPreffix.concat("""
                WHERE supplies.id = ?
                """);
        return jdbcTemplate.query(
                sql,
                new SupplyMapper(),
                id
        ).stream().findFirst();
    }

    Optional<Supply> getSupplyBySupply(Supply supply) {
        var sql = sqlPreffix.concat("""
                WHERE product_id = ? AND status = ? AND expected_date = ? AND amount = ?
                """);
        return jdbcTemplate.query(
                sql,
                new SupplyMapper(),
                supply.product().id(),
                supply.status(),
                supply.expectedDate(),
                supply.amount()
        ).stream().findFirst();
    }

    public Supply addSupply(Supply supply) {
        var sql = """
                INSERT INTO supplies (supplier_id, worker_id, status, arrival_date, processed_date, expected_date, product_id, amount)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(
            sql,
            supply.supplier().id(),
            null,
            supply.status(),
            null,
            null,
            supply.expectedDate(),
            supply.product().id(),
            supply.amount()
        );

        return getSupplyBySupply(supply).get();
    }

    public int deleteSupply(Long id) {
        var sql = "DELETE FROM supplies WHERE id = ?";
        return jdbcTemplate.update(
                sql,
                id
        );
    }

    public Supply updateSupply(Supply supply) {
        var sql = """
                UPDATE supplies
                SET supplier_id = ?, expected_date = ?, product_id = ?, amount = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(
                sql,
                supply.supplier().id(),
                supply.expectedDate(),
                supply.product().id(),
                supply.amount(),
                supply.id()
        );
        return getSupplyById(supply.id()).get();
    }

    public Supply acknowledgeSupply(Supply supply) {
        var sql = "UPDATE supplies SET status = ? WHERE id = ?";
        jdbcTemplate.update(
                sql,
                "arrived",
                supply.id()
        );
        return getSupplyById(supply.id()).get();
    }

    public Supply assignSupply(Supply supply) {
        var sql = "UPDATE supplies SET status = ?, worker_id = ? WHERE id = ?";
        jdbcTemplate.update(
                sql,
                "assigned",
                supply.worker().id(),
                supply.id()
        );
        return getSupplyById(supply.id()).get();
    }
//
//    public int updateSupply(Supply supply) {
//        var sql = """
//                UPDATE supplies
//                SET supplier_id = ?, worker_id = ?, status = ?, arrival_date = ?, processed_date = ?,
//                expected_date = ?, product_id = ?, amount = ?
//                WHERE id = ?
//                """;
//        return jdbcTemplate.update(
//                sql,
//                supply.supplierId(),
//                supply.workerId(),
//                supply.status(),
//                supply.arrivalDate(),
//                supply.processedDate(),
//                supply.expectedDate(),
//                supply.productId(),
//                supply.amount(),
//                supply.id()
//        );
//    }
//
//    public List<Supply> getSupplyByWorker(Long workerId) {
//        var sql = """
//                SELECT * FROM supplies
//                WHERE worker_id = ?
//                """;
//        return jdbcTemplate.query(
//                sql,
//                new SupplyMapper(),
//                workerId
//        );
//    }
//
//    public List<Supply> getSupplyByProduct(Long productId) {
//        var sql = """
//                SELECT * FROM supplies
//                WHERE product_id = ?
//                """;
//        return jdbcTemplate.query(
//                sql,
//                new SupplyMapper(),
//                productId
//        );
//    }
//
//    public List<Supply> getSupplyBySupplier(Long supplierId) {
//        var sql = """
//                SELECT * FROM supplies
//                WHERE supplier_id = ?
//                """;
//        return jdbcTemplate.query(
//                sql,
//                new SupplyMapper(),
//                supplierId
//        );
//    }
//
//    public int acknowledgeSupply(Supply supply) {
//        var sql = "UPDATE supplies SET status = ? WHERE id = ?";
//        return jdbcTemplate.update(
//                sql,
//                "arrived",
//                supply.id()
//        );
//    }
//
//    public int unpackSupply(Supply supply) {
//        var sql = "UPDATE supplies SET status = ? WHERE id = ?";
//        return jdbcTemplate.update(
//                sql,
//                "processed",
//                supply.id()
//        );
//    }
//
//    public int updateWorker(Supply supply) {
//        var sql = """
//                UPDATE supplies
//                SET worker_id = ?
//                WHERE id = ?
//                """;
//        return jdbcTemplate.update(
//                sql,
//                supply.workerId(),
//                supply.id()
//        );
//    }
//
//    public List<SupplyView> getAllSuppliesViews() {
//        var sql = sqlPreffix.concat(sqlSuffix);
//        return jdbcTemplate.query(
//                sql,
//                new SupplyViewMapper()
//        );
//    }
//
//    public List<SupplyView> getAllSuppliesViewsByWorkerUsername(String usernameSubstring) {
//        var sql = sqlPreffix.concat("""
//                WHERE worker.id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))""")
//                .concat(sqlSuffix);
//        return jdbcTemplate.query(
//                sql,
//                new SupplyViewMapper(),
//                "%" + usernameSubstring + "%"
//        );
//    }
//
//    public List<SupplyView> getAllSuppliesViewsBySupplierName(String name) {
//        var sql = sqlPreffix.concat("""
//                WHERE supplier.id IN (SELECT id FROM suppliers WHERE LOWER(suppliers.name) like LOWER((?)))
//                """).concat(sqlSuffix);
//        return jdbcTemplate.query(
//                sql,
//                new SupplyViewMapper(),
//                "%" + name + "%"
//        );
//    }
//
//    public List<SupplyView> getAllSuppliesViewsBySupplierNameAndUsername(String name, String username) {
//        var sql = sqlPreffix.concat("""
//                WHERE supplier.id IN (SELECT id FROM suppliers WHERE LOWER(suppliers.name) like LOWER((?))) AND
//                    worker.id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))
//                """).
//                concat(sqlSuffix);
//        return jdbcTemplate.query(
//                sql,
//                new SupplyViewMapper(),
//                "%" + name + "%",
//                "%" + username + "%"
//        );
//    }
//
//    public List<SupplyView> getSuppliesViewsByStatus(String status) {
//        var sql = sqlPreffix.concat("""
//                WHERE supplies.status = ?
//                """)
//                .concat(sqlSuffix);
//        return jdbcTemplate.query(
//                sql,
//                new SupplyViewMapper(),
//                status
//        );
//    }
//
//    public List<SupplyView> getSuppliesViewsByWorkerId(Long workerId) {
//        var sql = sqlPreffix.concat("""
//                WHERE supplies.worker_id = ?
//                """).
//                concat(sqlSuffix);
//        return jdbcTemplate.query(
//                sql,
//                new SupplyViewMapper(),
//                workerId
//        );
//    }
}