package warehouse.warehousemanagementsystem.supplier;

import warehouse.warehousemanagementsystem.address.Address;

public record Supplier(Long id, String name, Address address) {
}
