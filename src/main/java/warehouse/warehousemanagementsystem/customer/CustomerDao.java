package warehouse.warehousemanagementsystem.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Customer> getAllCustomers() {
        var sql = """
                SELECT * FROM customers
                ORDER BY id
                """;
        return jdbcTemplate.query(
                sql,
                new CustomerMapper()
        );
    }

    public Optional<Customer> getCustomerById(Long id) {
        var sql = """
                SELECT * FROM customers
                WHERE id = ?
                """;
        return jdbcTemplate.query(
                sql,
                new CustomerMapper(),
                id
        ).stream().findFirst();
    }

    public int addCustomer(Customer customer) {
        var sql = """
                INSERT INTO customers (name, last_name, address_id, email)
                VALUES (?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                customer.name(),
                customer.lastName(),
                customer.addressId(),
                customer.email()
        );
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        var sql = """
                SELECT * FROM customers
                WHERE email = ?
                """;
        return jdbcTemplate.query(
                sql,
                new CustomerMapper(),
                email
        ).stream().findFirst();
    }

    public Optional<Customer> getCustomerByData(Customer customer) {
        var sql = """
                SELECT * FROM customers
                WHERE name = ? AND last_name = ? AND address_id = ? AND email = ?
                """;
        return jdbcTemplate.query(
                sql,
                new CustomerMapper(),
                customer.name(),
                customer.lastName(),
                customer.addressId(),
                customer.email()
        ).stream().findFirst();
    }

}
