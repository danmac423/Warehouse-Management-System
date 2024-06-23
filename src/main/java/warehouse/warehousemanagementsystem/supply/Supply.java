package warehouse.warehousemanagementsystem.supply;

import warehouse.warehousemanagementsystem.product.Product;
import warehouse.warehousemanagementsystem.supplier.Supplier;
import warehouse.warehousemanagementsystem.worker.Worker;

import java.sql.Date;

public record Supply(
        Long id,
        Supplier supplier,
        Worker worker,
        String status,
        Date arrivalDate,
        Date processedDate,
        Date expectedDate,
        Product product,
        int amount) {
}
