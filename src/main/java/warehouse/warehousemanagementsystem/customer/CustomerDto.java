package warehouse.warehousemanagementsystem.customer;

import warehouse.warehousemanagementsystem.address.AddressDto;

public record CustomerDto(Long id, String name, String lastName, AddressDto address, String email) {
}
