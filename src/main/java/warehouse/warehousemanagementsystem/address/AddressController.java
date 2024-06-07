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

    @GetMapping("{id}")
    public Address getAddressById(@PathVariable Long id) {
        return addressService.getAddressById(id);
    }

    @PostMapping
    public int addAddress(@RequestBody Address address) {
        return addressService.addAddress(address);
    }

    @DeleteMapping("{id}")
    public int deleteAddress(@PathVariable Long id) {
        return addressService.deleteAddress(id);
    }

    @PutMapping
    public int updateAddress(@RequestBody Address address) {
        return addressService.updateAddress(address);
    }
}
