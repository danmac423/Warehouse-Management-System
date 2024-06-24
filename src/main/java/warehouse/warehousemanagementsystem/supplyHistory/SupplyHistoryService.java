package warehouse.warehousemanagementsystem.supplyHistory;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplyHistoryService {
    private final SupplyHistoryDao supplyHistoryDao;

    public SupplyHistoryService(SupplyHistoryDao supplyHistoryDao) {
        this.supplyHistoryDao = supplyHistoryDao;
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
