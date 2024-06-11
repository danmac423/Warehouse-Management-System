package warehouse.warehousemanagementsystem.product;

import java.math.BigDecimal;

public record ProductInOrder(Long id,
                             String name,
                             BigDecimal price,
                             String categoryName,
                             Integer amount,
                             BigDecimal totalPrice) {
}
