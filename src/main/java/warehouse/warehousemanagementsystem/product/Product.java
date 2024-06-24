package warehouse.warehousemanagementsystem.product;

import warehouse.warehousemanagementsystem.category.Category;

import java.math.BigDecimal;

public record Product(Long id, String name, BigDecimal price, Category category, int stock) {
}
