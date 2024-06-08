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
    public ResponseEntity<List<Supply>> getAllOrders() {
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

}
