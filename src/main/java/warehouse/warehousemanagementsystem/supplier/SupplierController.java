package warehouse.warehousemanagementsystem.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SupplierDto>> getSuppliers(
            @RequestParam (required = false) String supplierName,
            @RequestParam (required = false) String country,
            @RequestParam (required = false) String city
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(supplierService.getSupplies(supplierName, country, city));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<SupplierDto> addSupplier(@RequestBody SupplierDto supplier) {
        SupplierDto newSupplier = supplierService.addSupplierAndAddress(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSupplier);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.status(HttpStatus.OK).body("Supplier deleted successfully");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    public ResponseEntity<SupplierDto> updateProduct(@RequestBody SupplierDto supplier) {
        SupplierDto updatedSupplier = supplierService.updateSupplier(supplier);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSupplier);
    }
}
