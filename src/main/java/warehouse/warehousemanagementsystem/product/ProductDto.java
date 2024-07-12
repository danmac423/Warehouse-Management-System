package warehouse.warehousemanagementsystem.product;

import warehouse.warehousemanagementsystem.category.CategoryDto;

import java.math.BigDecimal;

public record ProductDto(Long id, String name, BigDecimal price, CategoryDto category, int stock) {
}
