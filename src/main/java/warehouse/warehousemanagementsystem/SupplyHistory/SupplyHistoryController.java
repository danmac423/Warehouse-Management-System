package warehouse.warehousemanagementsystem.SupplyHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliesHistory")
public class SupplyHistoryController {
    private final SupplyHistoryService supplyHistoryService;

    @Autowired
    public SupplyHistoryController(SupplyHistoryService supplyHistoryService) {
        this.supplyHistoryService = supplyHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<SupplyHistory>> getAllSupplies() {
        return new ResponseEntity<>(supplyHistoryService.getAllSupplies(), HttpStatus.OK);
    }

    @GetMapping("/worker/{workerId}")
    public List<SupplyHistory> getSuppliesByWorker(@PathVariable Long workerId) {
        return supplyHistoryService.getProductsByWorkerId(workerId);
    }

    @GetMapping("/product/{productName}")
    public List<SupplyHistory> getSuppliesByProduct(@PathVariable String productName) {
        return supplyHistoryService.getProductsByProductName(productName);
    }

    @GetMapping("/supplier/{supplierName}")
    public List<SupplyHistory> getSuppliesBySupplier(@PathVariable String supplierName) {
        return supplyHistoryService.getProductsBySupplierId(supplierName);
    }
}
