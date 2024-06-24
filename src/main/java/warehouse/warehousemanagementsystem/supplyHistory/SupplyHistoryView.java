package warehouse.warehousemanagementsystem.supplyHistory;

import java.sql.Date;

public record SupplyHistoryView(
        Long id,
        Long supplierId,
        String supplierName,
        Long workerId,
        String username,
        Date arrivalDate,
        Date expectedDate,
        Date processedDate,
        Long productId,
        String productName,
        int amount) {
}
