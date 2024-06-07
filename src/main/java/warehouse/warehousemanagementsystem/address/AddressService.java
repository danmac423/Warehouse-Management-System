package warehouse.warehousemanagementsystem.address;

import org.springframework.stereotype.Service;

@Service
public class AddressService {
    public final AddressDao addressDao;

    public AddressService(AddressDao addressDao) {
        this.addressDao = addressDao;
    }

    public java.util.List<Address> getAllAddresses() {
        return addressDao.getAllAddresses();
    }

    public Address getAddressById(Long id) {
        return addressDao.getAddressById(id);
    }

    public int addAddress(Address address) {
        return addressDao.addAddress(address);
    }

    public int deleteAddress(Long id) {
        return addressDao.deleteAddress(id);
    }

    public int updateAddress(Address address) {
        return addressDao.updateAddress(address);
    }
}
