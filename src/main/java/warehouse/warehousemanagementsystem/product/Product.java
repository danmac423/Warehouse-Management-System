package warehouse.warehousemanagementsystem.product;

import java.math.BigDecimal;

public record Product(Long id, String name, BigDecimal price, Long categoryId, int stock) {
}
