package warehouse.warehousemanagementsystem.address;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.ConflictException;
import warehouse.warehousemanagementsystem.exception.DatabaseException;
import warehouse.warehousemanagementsystem.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {
    public final AddressDao addressDao;

    public AddressService(AddressDao addressDao) {
        this.addressDao = addressDao;
    }

    public List<Address> getAllAddresses() {
        return addressDao.getAllAddresses();
    }

    public Address getAddressByData(Address address) {
        return addressDao.getAddressByData(address).orElseThrow(() -> new NotFoundException("Address not found"));
    }

    public void addAddress(Address address) {
        if (address.street().isEmpty()
                || address.houseNumber().isEmpty()
                || address.postalCode().isEmpty()
                || address.city().isEmpty()
                || address.country().isEmpty()) {
            throw new BadRequestException("All fields are required");
        }
        if (addressDao.getAddressByData(address).isPresent()) {
            throw new ConflictException("Address already exists");
        }
    }

    public void deleteAddress(Long id) {
        Optional<Address> address = addressDao.getAddressById(id);
        int result;
        if (address.isEmpty()) {
            throw new NotFoundException("Address not found");
        }
        try {
            result = addressDao.deleteAddress(id);
        } catch (Exception e) {
            throw new ConflictException("This address is still in use");
        }

        if (result != 1) {
            throw new DatabaseException("Failed to delete address");
        }
    }

    public void updateAddress(Address address) {
        if (addressDao.getAddressById(address.id()).isEmpty()) {
            throw new NotFoundException("Address not found");
        }
        if (addressDao.getAddressByData(address).isPresent()) {
            throw new ConflictException("Address already exists");
        }
        if (address.street().isEmpty()
                || address.houseNumber().isEmpty()
                || address.postalCode().isEmpty()
                || address.city().isEmpty()
                || address.country().isEmpty()) {
            throw new BadRequestException("All fields are required");
        }
    }
}
