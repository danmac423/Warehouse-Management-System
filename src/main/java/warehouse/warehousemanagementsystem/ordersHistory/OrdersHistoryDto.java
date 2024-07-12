package warehouse.warehousemanagementsystem.ordersHistory;

import java.util.Date;

public record OrdersHistoryDto(
        Long id,
        Long customerId,
        Date dateProcessed,
        Long workerId,
        Date dateReceived
) {
}
