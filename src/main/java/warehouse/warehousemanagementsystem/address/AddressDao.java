package warehouse.warehousemanagementsystem.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AddressDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AddressDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Address> getAddressById(Long id) {
        var sql = """
                SELECT * FROM addresses
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, new AddressMapper(), id)
                .stream().findFirst();
    }

    public Optional<Address> getAddressByData(Address address) {
        var sql = """
                SELECT * FROM addresses
                WHERE street = ? AND house_nr = ? AND postal_code = ? AND city = ? AND country = ?
                """;
        return jdbcTemplate.query(
                sql,
                new AddressMapper(),
                address.street(),
                address.houseNumber(),
                address.postalCode(),
                address.city(),
                address.country()
        ).stream().findFirst();
    }

    public Address addAddress(Address address) {
        var sql = """
                INSERT INTO addresses (street, house_nr, postal_code, city, country)
                VALUES (?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(
                sql,
                address.street(),
                address.houseNumber(),
                address.postalCode(),
                address.city(),
                address.country()
        );
        return getAddressByData(address).orElseThrow();
    }

    public Address updateAddress(Address address) {
        var sql = """
                UPDATE addresses
                SET street = ?, house_nr = ?, postal_code = ?, city = ?, country = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(
                sql,
                address.street(),
                address.houseNumber(),
                address.postalCode(),
                address.city(),
                address.country(),
                address.id()
        );
        return getAddressById(address.id()).orElseThrow();
    }
}
