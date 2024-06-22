package warehouse.warehousemanagementsystem.supply;

import java.sql.Date;

public record SupplyView(
        Long id,
        Long supplierId,
        String supplierName,
        Long workerId,
        String username,
        String status,
        Date arrivalDate,
        Date expectedDate,
        Long productId,
        String productName,
        int amount) {
}
