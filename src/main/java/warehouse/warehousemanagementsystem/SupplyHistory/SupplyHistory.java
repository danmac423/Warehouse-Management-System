package warehouse.warehousemanagementsystem.SupplyHistory;

import java.sql.Date;

public record SupplyHistory(
        Long id,
        Long supplierId,
        Long workerId,
        String status,
        Date arrivalDate,
        Date processedDate,
        Date expectedDate,
        Long productId,
        int amount) {
}
