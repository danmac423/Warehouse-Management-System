package warehouse.warehousemanagementsystem.orderView;

import java.math.BigDecimal;
import java.sql.Date;

public record OrderView(
        Long id,
        Long customerId,
        String name,
        String surname,
        String email,
        Date dateProcessed,
        Long workerId,
        String username,
        String status,
        Date dateReceived,
        BigDecimal totalPrice) {
}
