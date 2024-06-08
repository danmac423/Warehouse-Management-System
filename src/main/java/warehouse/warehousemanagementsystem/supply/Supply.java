package warehouse.warehousemanagementsystem.supply;

import java.sql.Date;

public record Supply(
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
