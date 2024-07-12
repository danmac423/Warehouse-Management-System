package warehouse.warehousemanagementsystem.supplyHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.supply.SupplyDto;

import java.util.List;

@Service
public class SupplyHistoryService {
    private final SupplyHistoryDao supplyHistoryDao;

    @Autowired
    public SupplyHistoryService(SupplyHistoryDao supplyHistoryDao) {
        this.supplyHistoryDao = supplyHistoryDao;
    }

    public List<SupplyDto> getSupplies(String supplierName, String workerUsername, String productName, String categoryName) {
        return supplyHistoryDao.getSupplies(supplierName, workerUsername, productName, categoryName);
    }

}
