package warehouse.warehousemanagementsystem.customer;

import warehouse.warehousemanagementsystem.address.Address;

public record Customer(Long id, String name, String lastName, Address address, String email) {
}
