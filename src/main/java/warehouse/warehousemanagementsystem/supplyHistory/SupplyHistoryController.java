package warehouse.warehousemanagementsystem.supplyHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import warehouse.warehousemanagementsystem.supply.SupplyDto;

import java.util.List;

@RestController
@RequestMapping("/api/supplies-history")
public class SupplyHistoryController {
    private final SupplyHistoryService supplyHistoryService;

    @Autowired
    public SupplyHistoryController(SupplyHistoryService supplyHistoryService) {
        this.supplyHistoryService = supplyHistoryService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SupplyDto>> getSupplies(
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String workerUsername,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String categoryName
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(supplyHistoryService.getSupplies(supplierName, workerUsername, productName, categoryName));
    }
}
