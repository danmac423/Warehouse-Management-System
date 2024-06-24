package warehouse.warehousemanagementsystem.supply;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.product.Product;
import warehouse.warehousemanagementsystem.product.ProductDao;
import warehouse.warehousemanagementsystem.supplier.Supplier;
import warehouse.warehousemanagementsystem.supplier.SupplierDao;
import warehouse.warehousemanagementsystem.worker.Worker;
import warehouse.warehousemanagementsystem.worker.WorkerDao;

import java.util.List;
import java.util.Objects;

@Service
public class SupplyService {
    private final SupplyDao supplyDao;
    private final SupplierDao supplierDao;
    private final WorkerDao workerDao;
    private final ProductDao productDao;

    public SupplyService(SupplyDao supplyDao, SupplierDao supplierDao, WorkerDao workerDao, ProductDao productDao) {
        this.supplyDao = supplyDao;
        this.supplierDao = supplierDao;
        this.workerDao = workerDao;
        this.productDao = productDao;
    }

    public List<Supply> getSupplies(String supplierName, String workerUsername, String productName, String status, Long workerId) {
        return supplyDao.getSupplies(supplierName, workerUsername, productName, status, workerId);
    }

    //
//    public List<Supply> getSuppliesByWorkerUsername(String username) {
//        return supplyDao.getSuppliesByWorkerUsername(username);
//    }
//
//    public List<Supply> getSuppliesByStatus(String status) {
//        return supplyDao.getSuppliesByStatus(status);
//    }
//
    @Transactional
    public Supply addSupply(Supply supply) {
        Supplier supplier = supply.supplier();
        if (supplier == null) {
            throw new BadRequestException("Supplier is required");
        }
        supplier = supplierDao.getSupplierById(supplier.id()).orElseThrow(() -> new BadRequestException("Supplier not found"));

        Worker worker = supply.worker();
        if (worker != null) {
            throw new BadRequestException("Worker cannot be assigned to the supply at the moment");
        }

        Product product = supply.product();
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

        Supply newSupply = new Supply(null, supplier, null, "underway", null, null, supply.expectedDate(), product, supply.amount());

        return supplyDao.addSupply(newSupply);
    }

    public void deleteSupply(Long id) {
        Supply supply = supplyDao.getSupplyById(id).orElseThrow(() -> new BadRequestException("Supply not found"));
        if (!supply.status().equals("underway")) {
            throw new BadRequestException("The supply must be underway to delete");
        }
        if (supplyDao.deleteSupply(id) != 1) {
            throw new BadRequestException("Failed to delete supply");
        }
    }

    public Supply updateSupply(Supply supply) {
        Supplier supplier = supply.supplier();
        if (supplier == null) {
            throw new BadRequestException("Supplier is required");
        }
        supplier = supplierDao.getSupplierById(supplier.id()).orElseThrow(() -> new BadRequestException("Supplier not found"));

        Worker worker = supply.worker();
        if (worker != null) {
            throw new BadRequestException("Worker cannot be assigned to the supply at the moment");
        }

        Product product = supply.product();
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

        Supply updatedSupply = new Supply(supply.id(), supplier, null, "underway", null, null, supply.expectedDate(), product, supply.amount());

        return supplyDao.updateSupply(updatedSupply);

    }

    public Supply acknowledgeSupply(Supply supply) {
        if (!supply.status().equals("underway")) {
            throw new BadRequestException("The supply must be underway to acknowledge");
        }

        return supplyDao.acknowledgeSupply(supply);
    }


    public Supply assignSupply(Supply supply) {
        if (!supply.status().equals("arrived") && !supply.status().equals("assigned")) {
            throw new BadRequestException("The supply must be arrived to assign");
        }

        if (supply.worker() == null || supply.worker().id() == null) {
            throw new BadRequestException("The supply must have a worker assigned to it");
        }

        return supplyDao.assignSupply(supply);
    }

    public void unpackSupply(Supply supply) {
        if (!supply.status().equals("arrived")) {
            throw new BadRequestException("The supply must be arrived to unpack");
        }

        if (supply.worker() == null || supply.worker().id() == null) {
            throw new BadRequestException("The supply must have a worker assigned to unpack");
        }

        supplyDao.unpackSupply(supply);
    }

//
//    public void updateSupply(Supply supply) {
//        long currentMilliseconds = System.currentTimeMillis();
//        if (supply.supplierId() == null
//                || supply.expectedDate() == null
//                || supply.productId() == null) {
//            throw new BadRequestException("All fields are required");
//        }
//        if (supply.amount() <= 0) {
//            throw new BadRequestException("Amount of the order must be bigger then 0");
//        }
//        if (supply.expectedDate().before(new Date(currentMilliseconds))) {
//            throw new BadRequestException("The expected date must be in the future");
//        }
//        if (supplyDao.updateSupply(supply) != 1) {
//            throw new DatabaseException("Failed to update supply");
//        }
//    }
//    public List<Supply> getSuppliesByWorkerId(Long workerId) { return supplyDao.getSupplyByWorker(workerId); }
//
//    public List<Supply> getSuppliesByProductId(Long productId) { return supplyDao.getSupplyByProduct(productId); }
//
//    public List<Supply> getSuppliesBySupplierId(Long supplierId) { return supplyDao.getSupplyBySupplier(supplierId); }
//
//    public void acknowledgeSupply(Supply supply) {
//        if (!supply.status().equals("underway")) {
//            throw new BadRequestException("The supply must be underway to acknowledge");
//        }
//
//        if (supplyDao.acknowledgeSupply(supply) != 1) {
//            throw new DatabaseException("Failed to acknowledge supply");
//        }
//    }
//
//    public void unpackSupply(Supply supply) {
//        if (!supply.status().equals("arrived")) {
//            throw new BadRequestException("The supply must have to unpack");
//        }
//
//        if (supply.workerId() != (null)) {
//            throw new BadRequestException("The supply must have a worker assigned to unpack");
//        }
//
//        if (supplyDao.unpackSupply(supply) != 1) {
//            throw new DatabaseException("Failed to acknowledge supply");
//        }
//    }
//
//    public void updateWorker(Supply supply) {
//        if (!supply.status().equals("arrived")) {
//            throw new BadRequestException("The supply must have arrived to add the worker");
//        }
//
//        if (supplyDao.updateWorker(supply) != 1) {
//            throw new DatabaseException("Failed to acknowledge supply");
//        }
//    }
//
//    public List<SupplyView> getAllSuppliesViews() { return supplyDao.getAllSuppliesViews(); }
//
//    public List<SupplyView> getSuppliesViewsByWorkerUsername(String username) { return supplyDao.getAllSuppliesViewsByWorkerUsername(username); }
//
//    public List<SupplyView> getSuppliesViewsBySupplierName(String name) { return supplyDao.getAllSuppliesViewsBySupplierName(name); }
//
//    public List<SupplyView> getSuppliesViewsBySupplierNameWorkerUsername(String name, String username) {
//        return supplyDao.getAllSuppliesViewsBySupplierNameAndUsername(name, username);
//    }
//
//    public List<SupplyView> getSuppliesViewsByStatus(String status) { return supplyDao.getSuppliesViewsByStatus(status); }
//
//    public List<SupplyView> getSuppliesViewsByWorkerId(Long workerId) { return supplyDao.getSuppliesViewsByWorkerId(workerId); }
}
