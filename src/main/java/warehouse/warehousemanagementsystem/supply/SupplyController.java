package warehouse.warehousemanagementsystem.supply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<Supply>> getSupplies(
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String workerUsername,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long workerId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(supplyService.getSupplies(supplierName, workerUsername, productName, status, workerId));
    }

    @PostMapping
    public ResponseEntity<Supply> addSupply(@RequestBody Supply supply) {
        Supply newSupply = supplyService.addSupply(supply);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSupply);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteSupply(@PathVariable Long id) {
        supplyService.deleteSupply(id);
        return ResponseEntity.status(HttpStatus.OK).body("Supply deleted successfully");
    }

    @PutMapping
    public ResponseEntity<Supply> updateSupply(@RequestBody Supply supply) {
        Supply updatedSupply = supplyService.updateSupply(supply);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSupply);
    }

    @PutMapping("/acknowledge")
    public ResponseEntity<Supply> acknowledgeSupply(@RequestBody Supply supply) {
        Supply acknowledgedSupply = supplyService.acknowledgeSupply(supply);
        return ResponseEntity.status(HttpStatus.OK).body(acknowledgedSupply);
    }

    @PutMapping("/assign")
    public ResponseEntity<Supply> assignSupply(@RequestBody Supply supply) {
        Supply assignedSupply = supplyService.assignSupply(supply);
        return ResponseEntity.status(HttpStatus.OK).body(assignedSupply);
    }

    @PutMapping("/unpack")
    public ResponseEntity<String> unpackSupply(@RequestBody Supply supply) {
        supplyService.unpackSupply(supply);
        return ResponseEntity.status(HttpStatus.OK).body("Supply unpacked successfully and added to the history");
    }
}
