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
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(supplyService.getSupplies(supplierName, workerUsername, productName, status));
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
//
//    @PutMapping("/unpack")
//    public ResponseEntity<String> unpackSupply(@RequestBody Supply supply) {
//        supplyService.unpackSupply(supply);
//        return new ResponseEntity<>("Supply unpacked successfully", HttpStatus.OK);
//    }
//
//    @PutMapping("/updateWorker")
//    public ResponseEntity<String> updateWorker(@RequestBody Supply supply) {
//        supplyService.updateWorker(supply);
//        return new ResponseEntity<>("Worker updated successfully", HttpStatus.OK);
//    }
//
//    @GetMapping("/formated")
//    public ResponseEntity<List<SupplyView>> getAllSuppliesViews() {
//        return new ResponseEntity<>(supplyService.getAllSuppliesViews(), HttpStatus.OK);
//    }
//
//    @GetMapping("/formated/username/{username}")
//    public ResponseEntity<List<SupplyView>> getSuppliesViewsByWorkerUsername(@PathVariable String username) {
//        return new ResponseEntity<>(supplyService.getSuppliesViewsByWorkerUsername(username), HttpStatus.OK);
//    }
//
//    @GetMapping("/formated/supplier/{name}")
//    public ResponseEntity<List<SupplyView>> getSuppliesViewsBySupplierName(@PathVariable String name) {
//        return new ResponseEntity<>(supplyService.getSuppliesViewsBySupplierName(name), HttpStatus.OK);
//    }
//
//    @GetMapping("/formated/supplier/{name}/username/{username}")
//    public ResponseEntity<List<SupplyView>> getSuppliesViewsBySupplierNameAndUsername(@PathVariable String name, @PathVariable String username) {
//        return new ResponseEntity<>(supplyService.getSuppliesViewsBySupplierNameWorkerUsername(name, username), HttpStatus.OK);
//    }
//
//    @GetMapping("/formated/status/{status}")
//    public ResponseEntity<List<SupplyView>> getSuppliesViewsByStatus(@PathVariable String status) {
//        return new ResponseEntity<>(supplyService.getSuppliesViewsByStatus(status), HttpStatus.OK);
//    }
//
//    @GetMapping("/formated/worker/{workerId}")
//    public ResponseEntity<List<SupplyView>> getSuppliesViewsByWorkerId(@PathVariable Long workerId) {
//        return new ResponseEntity<>(supplyService.getSuppliesViewsByWorkerId(workerId), HttpStatus.OK);
//    }
}
