package warehouse.warehousemanagementsystem.SupplyHistory;

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

    public List<SupplyHistory> getProductsByProductId(Long productId) { return supplyHistoryDao.getSupplyByProduct(productId); }

    public List<SupplyHistory> getProductsBySupplierId(Long supplierId) { return supplyHistoryDao.getSupplyBySupplier(supplierId); }

}
