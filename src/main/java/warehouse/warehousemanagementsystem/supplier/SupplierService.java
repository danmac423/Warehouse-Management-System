package warehouse.warehousemanagementsystem.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse.warehousemanagementsystem.address.AddressDto;
import warehouse.warehousemanagementsystem.address.AddressDao;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.ConflictException;
import warehouse.warehousemanagementsystem.exception.DatabaseException;
import warehouse.warehousemanagementsystem.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {
    private final SupplierDao supplierDao;
    private final AddressDao addressDao;

    @Autowired
    public SupplierService(SupplierDao supplierDao, AddressDao addressDao) {
        this.supplierDao = supplierDao;
        this.addressDao = addressDao;
    }

    public List<SupplierDto> getSupplies(String supplierName, String country, String city) {
        return supplierDao.getSuppliers(supplierName, country, city);
    }

    @Transactional
    public SupplierDto addSupplierAndAddress(SupplierDto supplier) {

        AddressDto address = supplier.address();
        if (supplier.name().isEmpty()
                || address.street().isEmpty()
                || address.houseNumber().isEmpty()
                || address.postalCode().isEmpty()
                || address.city().isEmpty()
                || address.country().isEmpty()) {
            throw new BadRequestException("All fields are required");
        }

        if (supplierDao.getSupplierByName(supplier.name()).isPresent()) {
            throw new ConflictException("Supplier with this name already exists");
        }

        address = addressDao.addAddress(address);

        SupplierDto newSupplier = new SupplierDto(
                supplier.id(),
                supplier.name(),
                address
        );

        return supplierDao.addSupplier(newSupplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {

        Optional<SupplierDto> supplier = supplierDao.getSupplierById(id);
        int result;
        if (supplier.isEmpty()) {
            throw new BadRequestException("Supplier not found");
        }
        try {
            result = supplierDao.deleteSupplier(id);
        } catch (Exception e) {
            throw new ConflictException("This supplier is still in use");
        }
        if (result != 1) {
            throw new DatabaseException("Failed to delete supplier");
        }
    }

    @Transactional
    public SupplierDto updateSupplier(SupplierDto supplier) {
        AddressDto address = supplier.address();
        if (supplier.name().isEmpty()
                || address.street().isEmpty()
                || address.houseNumber().isEmpty()
                || address.postalCode().isEmpty()
                || address.city().isEmpty()
                || address.country().isEmpty()) {
            throw new BadRequestException("All fields are required");
        }

        SupplierDto currentSupplier = supplierDao.getSupplierById(supplier.id()).orElseThrow(() -> new NotFoundException("Supplier not found"));
        AddressDto currentAddress = currentSupplier.address();

        if (supplierDao.getSupplierByName(supplier.name()).isPresent() && !currentSupplier.name().equals(supplier.name())) {
            throw new ConflictException("Supplier with this name already exists");
        }

        address = new AddressDto(
                currentAddress.id(),
                address.street(),
                address.houseNumber(),
                address.postalCode(),
                address.city(),
                address.country()
        );

        AddressDto updatedAddress = addressDao.updateAddress(address);

        return supplierDao.updateSupplier(new SupplierDto(
                supplier.id(),
                supplier.name(),
                updatedAddress
        ));

    }
}
