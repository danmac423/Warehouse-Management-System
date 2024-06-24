package warehouse.warehousemanagementsystem.supplyHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.warehousemanagementsystem.supply.Supply;
import warehouse.warehousemanagementsystem.supply.SupplyMapper;

import java.util.List;

@Repository
public class SupplyHistoryDao {
    private final JdbcTemplate jdbcTemplate;
    private final String sqlPrefix;
    private final String sqlSuffix;

    @Autowired
    public SupplyHistoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        sqlPrefix = """
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
""";
        sqlSuffix = """
                GROUP BY
                    supplies_history.id, supplier.id, supplier.name, worker.id, worker.username,
                    supplies_history.arrival_date, supplies_history.expected_date,
                	supplies_history, supplies_history.product_id, product.name, supplies_history.amount
                ORDER BY
                    supplies_history.id;
                """;
    }

    public List<Supply> getSupplies(String supplierName, String workerUsername, String productName, String categoryName) {
        if(supplierName == null) {
            supplierName = "";
        }
        if(workerUsername == null) {
            workerUsername = "";
        }
        if(productName == null) {
            productName = "";
        }
        if(categoryName == null) {
            categoryName = "";
        }
        var sql = """
                SELECT addresses.id AS address_id, addresses.street, addresses.house_nr, addresses.postal_code, addresses.city, addresses.country,
                suppliers.id AS supplier_id, suppliers.name AS supplier_name,
                workers.id AS worker_id, workers.username, workers.name AS worker_name, workers.last_name AS worker_last_name, workers.role, null AS password,
                categories.id AS category_id, categories.name AS category_name, null AS product_count,
                products.id AS product_id, products.name AS product_name, products.price, products.stock,
                supplies_history.id AS supply_id, 'processed' AS status, supplies_history.arrival_date, supplies_history.processed_date, supplies_history.expected_date, supplies_history.amount
                FROM supplies_history
                LEFT JOIN suppliers ON supplies_history.supplier_id = suppliers.id
                LEFT JOIN addresses ON suppliers.address_id = addresses.id
                LEFT JOIN workers ON supplies_history.worker_id = workers.id
                LEFT JOIN products ON supplies_history.product_id = products.id
                LEFT JOIN categories ON products.category_id = categories.id
                WHERE
                    LOWER(suppliers.name) LIKE LOWER(?) AND
                    LOWER(workers.username) LIKE LOWER(?) AND
                    LOWER(products.name) LIKE LOWER(?) AND
                    LOWER(categories.name) LIKE LOWER(?)
                ORDER BY supplies_history.id
                """;
        return jdbcTemplate.query(
                sql,
                new SupplyMapper(),
                "%" + supplierName + "%",
                "%" + workerUsername + "%",
                "%" + productName + "%",
                "%" + categoryName + "%"
        );
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
        var sql = sqlPrefix.concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryViewMapper()
        );
    }

    public List<SupplyHistoryView> getAllSuppliesHistViewsByWorkerUsername(String username) {
        var sql = sqlPrefix.concat("""
                WHERE worker.id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))
                """)
                .concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryViewMapper(),
                "%" + username + "%"
        );
    }

    public List<SupplyHistoryView> getAllSuppliesViewsHistBySupplierName(String name) {
        var sql = sqlPrefix.concat("""
                WHERE supplier.id IN (SELECT id FROM suppliers WHERE LOWER(suppliers.name) like LOWER((?)))
                """)
                .concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryViewMapper(),
                "%" + name + "%"
        );
    }

    public List<SupplyHistoryView> getSuppliesHistViewsBySupplierNameWorkerUsername(String name, String username) {
        var sql = sqlPrefix.concat("""
                WHERE supplier.id IN (SELECT id FROM suppliers WHERE LOWER(suppliers.name) like LOWER((?))) AND
                    worker.id IN (SELECT id FROM workers WHERE LOWER(workers.username) like LOWER((?)))
                """)
                .concat(sqlSuffix);
        return jdbcTemplate.query(
                sql,
                new SupplyHistoryViewMapper(),
                "%" + name + "%",
                "%" + username + "%"
        );
    }

}
