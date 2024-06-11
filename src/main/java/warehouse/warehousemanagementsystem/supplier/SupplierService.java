package warehouse.warehousemanagementsystem.supplier;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.ConflictException;
import warehouse.warehousemanagementsystem.exception.DatabaseException;
import warehouse.warehousemanagementsystem.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {
    private final SupplierDao supplierDao;

    public SupplierService(SupplierDao supplierDao) {
        this.supplierDao = supplierDao;
    }

    public List<Supplier> getAllSupplies() {
        return supplierDao.getAllSuppliers();
    }

    public void addSupplier(Supplier supplier) {
        if (supplier.name().isEmpty()
            || supplier.addressId() == null) {
            throw new BadRequestException("All fields are required");
        }
        if (supplierDao.getSupplierByName(supplier.name()).isPresent()) {
            throw new ConflictException("Product with this name already exists");
        }
        if (supplierDao.getSupplierByData(supplier).isPresent()) {
            throw new ConflictException("Product already exists");
        }
        if (supplierDao.addSupplier(supplier) != 1) {
            throw new DatabaseException("Failed to add product");
        }
    }

    public void deleteSupplier(Long id) {
        Optional<Supplier> supplier = supplierDao.getSupplierById(id);
        int result;
        if (supplier.isEmpty()) {
            throw new BadRequestException("Product not found");
        }
        try {
            result = supplierDao.deleteSupplier(id);
        } catch (Exception e) {
            throw new ConflictException("This product is still in use");
        }
        if (result != 1) {
            throw new DatabaseException("Failed to delete product");
        }
    }

    public void updateSupplier(Supplier supplier) {
        Supplier currentSupplier = supplierDao.getSupplierById(supplier.id()).orElseThrow(() -> new NotFoundException("Product not found"));
        if (supplier.name().isEmpty()
                || supplier.addressId() == null) {
            throw new BadRequestException("All fields are required");
        }
        if (supplierDao.getSupplierById(supplier.id()).isEmpty()) {
            throw new NotFoundException("Product not found");
        }
        if (supplierDao.getSupplierByData(supplier).isPresent()) {
            throw new ConflictException("Product already exists");
        }
        if (supplierDao.updateSupplier(supplier) != 1) {
            throw new DatabaseException("Failed to update product");
        }
    }

}
