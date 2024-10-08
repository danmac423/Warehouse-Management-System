package warehouse.warehousemanagementsystem.supply;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.product.ProductDto;
import warehouse.warehousemanagementsystem.product.ProductDao;
import warehouse.warehousemanagementsystem.supplier.SupplierDto;
import warehouse.warehousemanagementsystem.supplier.SupplierDao;
import warehouse.warehousemanagementsystem.worker.WorkerDto;

import java.util.List;
import java.util.Objects;

@Service
public class SupplyService {
    private final SupplyDao supplyDao;
    private final SupplierDao supplierDao;
    private final ProductDao productDao;

    public SupplyService(SupplyDao supplyDao, SupplierDao supplierDao, ProductDao productDao) {
        this.supplyDao = supplyDao;
        this.supplierDao = supplierDao;
        this.productDao = productDao;
    }

    public List<SupplyDto> getSupplies(String supplierName, String workerUsername, String productName, String status, Long workerId) {
        return supplyDao.getSupplies(supplierName, workerUsername, productName, status, workerId);
    }

    @Transactional
    public SupplyDto addSupply(SupplyDto supply) {
        SupplierDto supplier = supply.supplier();
        if (supplier == null) {
            throw new BadRequestException("Supplier is required");
        }
        supplier = supplierDao.getSupplierById(supplier.id()).orElseThrow(() -> new BadRequestException("Supplier not found"));

        WorkerDto worker = supply.worker();
        if (worker != null) {
            throw new BadRequestException("Worker cannot be assigned to the supply at the moment");
        }

        ProductDto product = supply.product();
        if (product == null) {
            throw new BadRequestException("Product is required");
        }
        product = productDao.getProductById(product.id()).orElseThrow(() -> new BadRequestException("Product not found"));

        if (!Objects.equals(supply.status(), "underway")) {
            throw new BadRequestException("The status of the supply must be 'underway'");
        }

        if (supply.arrivalDate() != null) {
            throw new BadRequestException("The arrival date must be empty");
        }

        if (supply.processedDate() != null) {
            throw new BadRequestException("The processed date must be empty");
        }

        if (supply.expectedDate() == null) {
            throw new BadRequestException("The expected date is required");
        }

        if (supply.amount() <= 0) {
            throw new BadRequestException("The amount of the supply must be bigger than 0");
        }

        if (supplyDao.getSupplyBySupply(supply).isPresent()) {
            throw new BadRequestException("The supply already exists");
        }

        SupplyDto newSupply = new SupplyDto(null, supplier, null, "underway", null, null, supply.expectedDate(), product, supply.amount());

        return supplyDao.addSupply(newSupply);
    }

    @Transactional
    public void deleteSupply(Long id) {
        SupplyDto supply = supplyDao.getSupplyById(id).orElseThrow(() -> new BadRequestException("Supply not found"));
        if (!supply.status().equals("underway")) {
            throw new BadRequestException("The supply must be underway to delete");
        }
        if (supplyDao.deleteSupply(id) != 1) {
            throw new BadRequestException("Failed to delete supply");
        }
    }

    @Transactional
    public SupplyDto updateSupply(SupplyDto supply) {
        SupplierDto supplier = supply.supplier();
        if (supplier == null) {
            throw new BadRequestException("Supplier is required");
        }
        supplier = supplierDao.getSupplierById(supplier.id()).orElseThrow(() -> new BadRequestException("Supplier not found"));

        WorkerDto worker = supply.worker();
        if (worker != null) {
            throw new BadRequestException("Worker cannot be assigned to the supply at the moment");
        }

        ProductDto product = supply.product();
        if (product == null) {
            throw new BadRequestException("Product is required");
        }
        product = productDao.getProductById(product.id()).orElseThrow(() -> new BadRequestException("Product not found"));

        if (!Objects.equals(supply.status(), "underway")) {
            throw new BadRequestException("The status of the supply must be 'underway'");
        }

        if (supply.arrivalDate() != null) {
            throw new BadRequestException("The arrival date must be empty");
        }

        if (supply.processedDate() != null) {
            throw new BadRequestException("The processed date must be empty");
        }

        if (supply.expectedDate() == null) {
            throw new BadRequestException("The expected date is required");
        }

        if (supply.amount() <= 0) {
            throw new BadRequestException("The amount of the supply must be bigger than 0");
        }

        if (supplyDao.getSupplyBySupply(supply).isPresent() && !supplyDao.getSupplyBySupply(supply).get().id().equals(supply.id())) {
            throw new BadRequestException("The supply already exists");
        }

        SupplyDto updatedSupply = new SupplyDto(supply.id(), supplier, null, "underway", null, null, supply.expectedDate(), product, supply.amount());

        return supplyDao.updateSupply(updatedSupply);

    }

    @Transactional
    public SupplyDto acknowledgeSupply(SupplyDto supply) {
        SupplyDto currentSupply = supplyDao.getSupplyById(supply.id()).orElseThrow(() -> new BadRequestException("Supply not found"));
        if (!currentSupply.status().equals("underway")) {
            throw new BadRequestException("The supply must be underway to acknowledge");
        }

        return supplyDao.acknowledgeSupply(supply);
    }

    @Transactional
    public SupplyDto assignSupply(SupplyDto supply) {
        SupplyDto currentSupply = supplyDao.getSupplyById(supply.id()).orElseThrow(() -> new BadRequestException("Supply not found"));
        if (!currentSupply.status().equals("arrived")) {
            throw new BadRequestException("The supply must be arrived to assign");
        }

        if (supply.worker() == null || supply.worker().id() == null) {
            throw new BadRequestException("You must provide a worker to assign the supply to");
        }

        return supplyDao.assignSupply(supply);
    }

    @Transactional
    public void unpackSupply(SupplyDto supply) {
        SupplyDto currentSupply = supplyDao.getSupplyById(supply.id()).orElseThrow(() -> new BadRequestException("Supply not found"));
        if (!currentSupply.status().equals("arrived")) {
            throw new BadRequestException("The supply must be arrived to unpack");
        }

        if (supply.worker() == null || supply.worker().id() == null) {
            throw new BadRequestException("The supply must have a worker assigned to unpack");
        }

        supplyDao.unpackSupply(supply);
    }
}
