package warehouse.warehousemanagementsystem.address;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Address> getAllAddresses() {
        return addressService.getAllAddresses();
    }

    @PostMapping
    public void addAddress(@RequestBody Address address) {
        addressService.addAddress(address);
    }

    @DeleteMapping("{id}")
    public void deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
    }

    @PutMapping
    public void updateAddress(@RequestBody Address address) {
        addressService.updateAddress(address);
    }
}
