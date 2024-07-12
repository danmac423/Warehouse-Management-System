package warehouse.warehousemanagementsystem.order;

import warehouse.warehousemanagementsystem.customer.CustomerDto;
import warehouse.warehousemanagementsystem.product.ProductInOrderDto;
import warehouse.warehousemanagementsystem.worker.WorkerDto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public record OrderDto(
        Long id,
        CustomerDto customer,
        Date dateProcessed,
        WorkerDto worker,
        String status,
        Date dateReceived,
        BigDecimal totalPrice,
        List<ProductInOrderDto> products) {
}
