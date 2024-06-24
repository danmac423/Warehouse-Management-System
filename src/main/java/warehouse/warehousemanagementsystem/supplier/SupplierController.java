package warehouse.warehousemanagementsystem.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<List<Supplier>> getSuppliers(
            @RequestParam (required = false) String supplierName,
            @RequestParam (required = false) String country,
            @RequestParam (required = false) String city
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(supplierService.getSupplies(supplierName, country, city));
    }

    @PostMapping
    public ResponseEntity<Supplier> addSupplier(@RequestBody Supplier supplier) {
        Supplier newSupplier = supplierService.addSupplierAndAddress(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSupplier);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.status(HttpStatus.OK).body("Supplier deleted successfully");
    }

    @PutMapping
    public ResponseEntity<Supplier> updateProduct(@RequestBody Supplier supplier) {
        Supplier updatedSupplier = supplierService.updateSupplier(supplier);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSupplier);
    }
}
