package warehouse.warehousemanagementsystem.supply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplies")
public class SupplyController {
    private final SupplyService supplyService;

    @Autowired
    public SupplyController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')")
    @GetMapping
    public ResponseEntity<List<SupplyDto>> getSupplies(
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String workerUsername,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long workerId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(supplyService.getSupplies(supplierName, workerUsername, productName, status, workerId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<SupplyDto> addSupply(@RequestBody SupplyDto supply) {
        SupplyDto newSupply = supplyService.addSupply(supply);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSupply);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSupply(@PathVariable Long id) {
        supplyService.deleteSupply(id);
        return ResponseEntity.status(HttpStatus.OK).body("Supply deleted successfully");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    public ResponseEntity<SupplyDto> updateSupply(@RequestBody SupplyDto supply) {
        SupplyDto updatedSupply = supplyService.updateSupply(supply);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSupply);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/acknowledge")
    public ResponseEntity<SupplyDto> acknowledgeSupply(@RequestBody SupplyDto supply) {
        SupplyDto acknowledgedSupply = supplyService.acknowledgeSupply(supply);
        return ResponseEntity.status(HttpStatus.OK).body(acknowledgedSupply);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/assign")
    public ResponseEntity<SupplyDto> assignSupply(@RequestBody SupplyDto supply) {
        SupplyDto assignedSupply = supplyService.assignSupply(supply);
        return ResponseEntity.status(HttpStatus.OK).body(assignedSupply);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')")
    @PutMapping("/unpack")
    public ResponseEntity<String> unpackSupply(@RequestBody SupplyDto supply) {
        supplyService.unpackSupply(supply);
        return ResponseEntity.status(HttpStatus.OK).body("Supply unpacked successfully and added to the history");
    }
}
