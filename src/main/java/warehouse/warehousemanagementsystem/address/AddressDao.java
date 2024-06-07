package warehouse.warehousemanagementsystem.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AddressDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AddressDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Address> getAllAddresses() {
        var sql = """
                SELECT * FROM addresses
                """;
        return jdbcTemplate.query(
                sql,
                new AddressMapper()
        );
    }

    public Address getAddressById(Long id) {
        var sql = """
                SELECT * FROM addresses
                WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(
                sql,
                new AddressMapper(),
                id
        );
    }

    public int addAddress(Address address) {
        var sql = """
                INSERT INTO addresses (street, house_nr, postal_code, city, country)
                VALUES (?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                sql,
                address.street(),
                address.houseNumber(),
                address.postalCode(),
                address.city(),
                address.country()
        );
    }

    public int deleteAddress(Long id) {
        var sql = """
                DELETE FROM addresses
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                id
        );
    }

    public int updateAddress(Address address) {
        var sql = """
                UPDATE addresses
                SET street = ?, house_nr = ?, postal_code = ?, city = ?, country = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                address.street(),
                address.houseNumber(),
                address.postalCode(),
                address.city(),
                address.country(),
                address.id()
        );
    }
}
