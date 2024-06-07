package warehouse.warehousemanagementsystem.customer;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.getAllCustomers();
    }

    public void addCustomer(Customer customer) {
        if (customer.name().isEmpty()
            || customer.lastName().isEmpty()
            || customer.email().isEmpty()) {
            throw new BadRequestException("All fields are required");
        }
        if (customerDao.getCustomerByEmail(customer.email()).isPresent()) {
            throw new BadRequestException("Customer with this email already exists");
        }
        if (customerDao.getCustomerByData(customer).isPresent()) {
            throw new BadRequestException("Customer already exists");
        }
        if (customerDao.addCustomer(customer) != 1) {
            throw new BadRequestException("Failed to add customer");
        }
    }
}
