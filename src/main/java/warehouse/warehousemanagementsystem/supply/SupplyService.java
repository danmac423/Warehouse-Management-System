package warehouse.warehousemanagementsystem.supply;

import jdk.jshell.Snippet;
import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.DatabaseException;

import java.util.Date;
import java.util.List;

@Service
public class SupplyService {
    private final SupplyDao supplyDao;

    public SupplyService(SupplyDao supplyDao) {
        this.supplyDao = supplyDao;
    }

    public List<Supply> getAllSupplies() {
        return supplyDao.getAllSupplies();
    }

    public void addSupply(Supply supply) {
        long currentMilliseconds = System.currentTimeMillis();
        if (supply.supplierId() == null
                || supply.expectedDate() == null
                || supply.productId() == null) {
            throw new BadRequestException("All fields are required");
        }
        if (supply.amount() <= 0) {
            throw new BadRequestException("Amount of the order must be bigger then 0");
        }
        if (supply.expectedDate().before(new Date(currentMilliseconds))) {
            throw new BadRequestException("The expected date must be in the future");
        }
        if (supplyDao.addSupply(supply) != 1) {
            throw new DatabaseException("Failed to update supply");
        }
    }

    public void updateSupply(Supply supply) {
        long currentMilliseconds = System.currentTimeMillis();
        if (supply.supplierId() == null
                || supply.expectedDate() == null
                || supply.productId() == null) {
            throw new BadRequestException("All fields are required");
        }
        if (supply.amount() <= 0) {
            throw new BadRequestException("Amount of the order must be bigger then 0");
        }
        if (supply.expectedDate().before(new Date(currentMilliseconds))) {
            throw new BadRequestException("The expected date must be in the future");
        }
        if (supplyDao.updateSupply(supply) != 1) {
            throw new DatabaseException("Failed to update supply");
        }
    }
    public List<Supply> getSuppliesByWorkerId(Long workerId) { return supplyDao.getSupplyByWorker(workerId); }

    public List<Supply> getSuppliesByProductId(Long productId) { return supplyDao.getSupplyByProduct(productId); }

    public List<Supply> getSuppliesBySupplierId(Long supplierId) { return supplyDao.getSupplyBySupplier(supplierId); }

    public void acknowledgeSupply(Supply supply) {
        if (!supply.status().equals("underway")) {
            throw new BadRequestException("The supply must be underway to acknowledge");
        }

        if (supplyDao.acknowledgeSupply(supply) != 1) {
            throw new DatabaseException("Failed to acknowledge supply");
        }
    }

    public void unpackSupply(Supply supply) {
        if (!supply.status().equals("arrived")) {
            throw new BadRequestException("The supply must have to unpack");
        }

        if (supply.workerId() != (null)) {
            throw new BadRequestException("The supply must have a worker assigned to unpack");
        }

        if (supplyDao.unpackSupply(supply) != 1) {
            throw new DatabaseException("Failed to acknowledge supply");
        }
    }

    public void updateWorker(Supply supply) {
        if (!supply.status().equals("arrived")) {
            throw new BadRequestException("The supply must have arrived to add the worker");
        }

        if (supplyDao.updateWorker(supply) != 1) {
            throw new DatabaseException("Failed to acknowledge supply");
        }
    }

    public List<SupplyView> getAllSuppliesViews() { return supplyDao.getAllSuppliesViews(); }

    public List<SupplyView> getSuppliesViewsByWorkerUsername(String username) { return supplyDao.getAllSuppliesViewsByWorkerUsername(username); }

    public List<SupplyView> getSuppliesViewsBySupplierName(String name) { return supplyDao.getAllSuppliesViewsBySupplierName(name); }

    public List<SupplyView> getSuppliesViewsBySupplierNameWorkerUsername(String name, String username) {
        return supplyDao.getAllSuppliesViewsBySupplierNameAndUsername(name, username);
    }

    public List<SupplyView> getSuppliesViewsByStatus(String status) { return supplyDao.getSuppliesViewsByStatus(status); }
}
