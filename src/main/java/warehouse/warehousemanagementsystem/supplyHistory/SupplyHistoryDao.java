package warehouse.warehousemanagementsystem.supplyHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.warehousemanagementsystem.supply.SupplyDto;
import warehouse.warehousemanagementsystem.supply.SupplyMapper;

import java.util.List;

@Repository
public class SupplyHistoryDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SupplyHistoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SupplyDto> getSupplies(String supplierName, String workerUsername, String productName, String categoryName) {
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

}
