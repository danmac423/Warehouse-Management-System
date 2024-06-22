package warehouse.warehousemanagementsystem.supplier;

public record SupplierView(
        Long id,
        String name,
        Long addressId,
        String street,
        String houseNr,
        String postalCode,
        String city,
        String country) {
}
