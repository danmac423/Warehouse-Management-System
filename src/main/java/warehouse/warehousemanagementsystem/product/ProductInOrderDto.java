package warehouse.warehousemanagementsystem.product;

import warehouse.warehousemanagementsystem.category.CategoryDto;

import java.math.BigDecimal;

public record ProductInOrderDto(Long id,
                                String name,
                                BigDecimal price,
                                CategoryDto category,
                                Integer amount,
                                BigDecimal totalPrice) {
}
