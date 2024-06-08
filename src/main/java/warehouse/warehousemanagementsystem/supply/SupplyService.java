package warehouse.warehousemanagementsystem.supply;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;

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
            throw new BadRequestException("Failed to update supply");
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
            throw new BadRequestException("Failed to update supply");
        }
    }
}
