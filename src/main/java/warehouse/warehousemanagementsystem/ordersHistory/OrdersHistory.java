package warehouse.warehousemanagementsystem.ordersHistory;

import warehouse.warehousemanagementsystem.product.Product;

import java.util.Date;
import java.util.List;

public record OrdersHistory(
        Long id,
        Long customerId,
        Date dateProcessed,
        Long workerId,
        Date dateReceived
) {
}
