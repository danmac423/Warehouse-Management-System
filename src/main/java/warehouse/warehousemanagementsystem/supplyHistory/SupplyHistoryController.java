package warehouse.warehousemanagementsystem.supplyHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import warehouse.warehousemanagementsystem.supply.Supply;

import java.util.List;

@RestController
@RequestMapping("/api/supplies-history")
public class SupplyHistoryController {
    private final SupplyHistoryService supplyHistoryService;

    @Autowired
    public SupplyHistoryController(SupplyHistoryService supplyHistoryService) {
        this.supplyHistoryService = supplyHistoryService;
    }

//    @GetMapping
//    public ResponseEntity<List<SupplyHistory>> getAllSupplies() {
//        return new ResponseEntity<>(supplyHistoryService.getAllSupplies(), HttpStatus.OK);
//    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Supply>> getSupplies(
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String workerUsername,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String categoryName
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(supplyHistoryService.getSupplies(supplierName, workerUsername, productName, categoryName));
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

    @GetMapping("/formated")
    public ResponseEntity<List<SupplyHistoryView>> getAllSuppliesHistViews() {
        return new ResponseEntity<>(supplyHistoryService.getAllSuppliesHistViews(), HttpStatus.OK);
    }

    @GetMapping("/formated/username/{username}")
    public ResponseEntity<List<SupplyHistoryView>> getSuppliesHistViewByUsername(@PathVariable String username) {
        return new ResponseEntity<>(supplyHistoryService.getSuppliesHistViewsByWorkerUsername(username), HttpStatus.OK);
    }

    @GetMapping("/formated/supplier/{name}")
    public ResponseEntity<List<SupplyHistoryView>> getSuppliesHistViewBySupplierName(@PathVariable String name) {
        return new ResponseEntity<>(supplyHistoryService.getSuppliesHistViewsBySupplierName(name), HttpStatus.OK);
    }

    @GetMapping("/formated/supplier/{name}/username/{username}")
    public ResponseEntity<List<SupplyHistoryView>> getSuppliesHistViewsBySupplierNameWorkerUsername(@PathVariable String name, @PathVariable String username) {
        return new ResponseEntity<>(supplyHistoryService.getSuppliesHistViewsBySupplierNameWorkerUsername(name, username    ), HttpStatus.OK);
    }
}
