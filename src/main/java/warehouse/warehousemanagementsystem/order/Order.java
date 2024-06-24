package warehouse.warehousemanagementsystem.order;

import warehouse.warehousemanagementsystem.customer.Customer;
import warehouse.warehousemanagementsystem.product.ProductInOrder;
import warehouse.warehousemanagementsystem.worker.Worker;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public record Order(
        Long id,
        Customer customer,
        Date dateProcessed,
        Worker worker,
        String status,
        Date dateReceived,
        BigDecimal totalPrice,
        List<ProductInOrder> products) {
}
