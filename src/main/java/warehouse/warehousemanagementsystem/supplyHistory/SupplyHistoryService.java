package warehouse.warehousemanagementsystem.supplyHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.supply.Supply;

import java.util.List;

@Service
public class SupplyHistoryService {
    private final SupplyHistoryDao supplyHistoryDao;

    @Autowired
    public SupplyHistoryService(SupplyHistoryDao supplyHistoryDao) {
        this.supplyHistoryDao = supplyHistoryDao;
    }

    public List<Supply> getSupplies(String supplierName, String workerUsername, String productName, String categoryName) {
        return supplyHistoryDao.getSupplies(supplierName, workerUsername, productName, categoryName);
    }

    public List<SupplyHistory> getAllSupplies() {
        return supplyHistoryDao.getAllSupplies();
    }

    public List<SupplyHistory> getProductsByWorkerId(Long workerId) { return supplyHistoryDao.getSupplyByWorker(workerId); }

    public List<SupplyHistory> getProductsByProductName(String productName) { return supplyHistoryDao.getSupplyByProduct(productName); }

    public List<SupplyHistory> getProductsBySupplierId(String supplierName) { return supplyHistoryDao.getSupplyBySupplier(supplierName); }

    public List<SupplyHistoryView> getAllSuppliesHistViews() { return supplyHistoryDao.getAllSuppliesHistViews(); }

    public List<SupplyHistoryView> getSuppliesHistViewsByWorkerUsername(String username) { return supplyHistoryDao.getAllSuppliesHistViewsByWorkerUsername(username); }

    public List<SupplyHistoryView> getSuppliesHistViewsBySupplierName(String name) { return supplyHistoryDao.getAllSuppliesViewsHistBySupplierName(name); }

    public List<SupplyHistoryView> getSuppliesHistViewsBySupplierNameWorkerUsername(String name, String username) {
        return supplyHistoryDao.getSuppliesHistViewsBySupplierNameWorkerUsername(name, username);
    }

}
