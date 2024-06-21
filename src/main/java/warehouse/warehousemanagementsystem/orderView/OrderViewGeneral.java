package warehouse.warehousemanagementsystem.orderView;

import java.math.BigDecimal;
import java.sql.Date;

public record OrderViewGeneral(
        Long id,
        String email,
        Date dateProcessed,
        String username,
        String status,
        Date dateReceived,
        BigDecimal totalPrice) {
}
