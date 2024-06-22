package warehouse.warehousemanagementsystem.ordersHistory;

import java.math.BigDecimal;
import java.sql.Date;

public record OrdersHistoryView(
        Long id,
        Long customerId,
        String name,
        String surname,
        String email,
        java.sql.Date dateProcessed,
        Long workerId,
        String username,
        Date dateReceived,
        BigDecimal totalPrice) {
}
