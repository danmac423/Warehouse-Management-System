package warehouse.warehousemanagementsystem.customer;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.ConflictException;
import warehouse.warehousemanagementsystem.exception.DatabaseException;
import warehouse.warehousemanagementsystem.exception.NotFoundException;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        var customers = customerDao.getAllCustomers();
        if (customers.isEmpty()) {
            throw new NotFoundException("No customers found");
        }
        return customers;
    }

    public void addCustomer(Customer customer) {
        if (customer.name().isEmpty()
            || customer.lastName().isEmpty()
            || customer.email().isEmpty()) {
            throw new BadRequestException("All fields are required");
        }
        if (customerDao.getCustomerByEmail(customer.email()).isPresent()) {
            throw new ConflictException("Customer with this email already exists");
        }
        if (customerDao.getCustomerByData(customer).isPresent()) {
            throw new ConflictException("Customer already exists");
        }
        if (customerDao.addCustomer(customer) != 1) {
            throw new DatabaseException("Failed to add customer");
        }
    }
}
