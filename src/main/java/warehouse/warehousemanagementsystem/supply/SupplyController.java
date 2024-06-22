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
    public ResponseEntity<List<Supply>> getAllSupplies() {
        return new ResponseEntity<>(supplyService.getAllSupplies(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addSupply(@RequestBody Supply supply) {
        supplyService.addSupply(supply);
        return new ResponseEntity<>("Supply added successfully", HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<String> updateSupply(@RequestBody Supply supply) {
        supplyService.updateSupply(supply);
        return new ResponseEntity<>("Supply updated successfully", HttpStatus.OK);
    }

    @GetMapping("/worker/{workerId}")
    public List<Supply> getSuppliesByWorker(@PathVariable Long workerId) {
        return supplyService.getSuppliesByWorkerId(workerId);
    }

    @GetMapping("/product/{productId}")
    public List<Supply> getSuppliesByProduct(@PathVariable Long productId) {
        return supplyService.getSuppliesByProductId(productId);
    }

    @GetMapping("/supplier/{supplierId}")
    public List<Supply> getSuppliesBySupplier(@PathVariable Long supplierId) {
        return supplyService.getSuppliesBySupplierId(supplierId);
    }

    @PutMapping("/acknowledge")
    public ResponseEntity<String> acknowledgeSupply(@RequestBody Supply supply) {
        supplyService.acknowledgeSupply(supply);
        return new ResponseEntity<>("Supply acknowledged successfully", HttpStatus.OK);
    }

    @PutMapping("/unpack")
    public ResponseEntity<String> unpackSupply(@RequestBody Supply supply) {
        supplyService.unpackSupply(supply);
        return new ResponseEntity<>("Supply unpacked successfully", HttpStatus.OK);
    }

    @PutMapping("/updateWorker")
    public ResponseEntity<String> updateWorker(@RequestBody Supply supply) {
        supplyService.updateWorker(supply);
        return new ResponseEntity<>("Worker updated successfully", HttpStatus.OK);
    }

    @GetMapping("/formated")
    public ResponseEntity<List<SupplyView>> getAllSuppliesViews() {
        return new ResponseEntity<>(supplyService.getAllSuppliesViews(), HttpStatus.OK);
    }

    @GetMapping("/formated/username/{username}")
    public ResponseEntity<List<SupplyView>> getSuppliesViewsByWorkerUsername(@PathVariable String username) {
        return new ResponseEntity<>(supplyService.getSuppliesViewsByWorkerUsername(username), HttpStatus.OK);
    }

    @GetMapping("/formated/supplier/{name}")
    public ResponseEntity<List<SupplyView>> getSuppliesViewsBySupplierName(@PathVariable String name) {
        return new ResponseEntity<>(supplyService.getSuppliesViewsBySupplierName(name), HttpStatus.OK);
    }

    @GetMapping("/formated/supplier/{name}/username/{username}")
    public ResponseEntity<List<SupplyView>> getSuppliesViewsBySupplierNameAndUsername(@PathVariable String name, @PathVariable String username) {
        return new ResponseEntity<>(supplyService.getSuppliesViewsBySupplierNameWorkerUsername(name, username), HttpStatus.OK);
    }
}
