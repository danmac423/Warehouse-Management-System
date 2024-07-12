package warehouse.warehousemanagementsystem.supply;

import warehouse.warehousemanagementsystem.product.ProductDto;
import warehouse.warehousemanagementsystem.supplier.SupplierDto;
import warehouse.warehousemanagementsystem.worker.WorkerDto;

import java.sql.Date;

public record SupplyDto(
        Long id,
        SupplierDto supplier,
        WorkerDto worker,
        String status,
        Date arrivalDate,
        Date processedDate,
        Date expectedDate,
        ProductDto product,
        int amount) {
}
