package warehouse.warehousemanagementsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class RefreshTokenDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RefreshTokenDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveRefreshToken(String username, String refreshToken, Date createdDate, Date expirationDate) {
        var sql = """
                INSERT INTO refresh_tokens (username, token, created_at, expires_at)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (username)
                DO UPDATE SET
                    token = EXCLUDED.token,
                    created_at = EXCLUDED.created_at,
                    expires_at = EXCLUDED.expires_at
                """;
        jdbcTemplate.update(sql, username, refreshToken, createdDate, expirationDate);

    }

    public String getRefreshToken(String username) {
        var sql = """
                SELECT token
                FROM refresh_tokens
                WHERE username = ?
                """;
        return jdbcTemplate.queryForObject(sql, String.class, username);
    }

}
