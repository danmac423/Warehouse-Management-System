package warehouse.warehousemanagementsystem.supplyHistory;

import java.sql.Date;

public record SupplyHistory(
        Long id,
        Long supplierId,
        Long workerId,
        Date arrivalDate,
        Date processedDate,
        Date expectedDate,
        Long productId,
        int amount) {
}
