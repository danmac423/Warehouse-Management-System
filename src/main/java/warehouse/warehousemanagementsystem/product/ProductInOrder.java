package warehouse.warehousemanagementsystem.product;

import warehouse.warehousemanagementsystem.category.Category;

import java.math.BigDecimal;

public record ProductInOrder(Long id,
                             String name,
                             BigDecimal price,
                             Category category,
                             Integer amount,
                             BigDecimal totalPrice) {
}
