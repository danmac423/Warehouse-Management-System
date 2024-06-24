package warehouse.warehousemanagementsystem.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Address>> getAllAddresses() {
        return new ResponseEntity<>(addressService.getAllAddresses(), HttpStatus.OK);
    }

    @GetMapping("address-data")
    public ResponseEntity<Address> getAddressByData(@RequestBody Address address) {
        return new ResponseEntity<>(addressService.getAddressByData(address), HttpStatus.OK);
    }

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addAddress(@RequestBody Address address) {
        addressService.addAddress(address);
        return new ResponseEntity<>("Address added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return new ResponseEntity<>("Address deleted successfully", HttpStatus.OK);
    }

    @PutMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateAddress(@RequestBody Address address) {
        addressService.updateAddress(address);
        return new ResponseEntity<>("Address updated successfully", HttpStatus.OK);
    }
}
