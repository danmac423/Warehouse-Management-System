package warehouse.warehousemanagementsystem.order;

import java.math.BigDecimal;
import java.sql.Date;

public record Order(
        Long id,
        Long customerId,
        Date dateProcessed,
        Long workerId,
        String status,
        Date dateReceived,
        BigDecimal totalPrice) {
}
