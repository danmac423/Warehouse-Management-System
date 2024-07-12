package warehouse.warehousemanagementsystem.supplier;

import warehouse.warehousemanagementsystem.address.AddressDto;

public record SupplierDto(Long id, String name, AddressDto address) {
}
