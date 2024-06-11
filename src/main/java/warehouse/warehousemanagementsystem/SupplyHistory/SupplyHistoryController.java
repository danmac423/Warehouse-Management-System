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
    public ResponseEntity<List<SupplyHistory>> getAllOrders() {
        return new ResponseEntity<>(supplyHistoryService.getAllSupplies(), HttpStatus.OK);
    }

    @GetMapping("/worker/{workerId}")
    public List<SupplyHistory> getProductsByWorker(@PathVariable Long workerId) {
        return supplyHistoryService.getProductsByWorkerId(workerId);
    }

    @GetMapping("/product/{productId}")
    public List<SupplyHistory> getProductsByProduct(@PathVariable Long productId) {
        return supplyHistoryService.getProductsByProductId(productId);
    }

    @GetMapping("/supplier/{supplierId}")
    public List<SupplyHistory> getProductsBySupplier(@PathVariable Long supplierId) {
        return supplyHistoryService.getProductsBySupplierId(supplierId);
    }
}
