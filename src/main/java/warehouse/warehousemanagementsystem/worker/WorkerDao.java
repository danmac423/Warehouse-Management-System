package warehouse.warehousemanagementsystem.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WorkerDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public WorkerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Worker> getWorkers(String username, String role) {
        if (username == null) {
            username = "";
        }
        if (role == null) {
            role = "";
        }
        var sql = """
                SELECT * FROM workers
                WHERE LOWER(username) LIKE LOWER(?) AND LOWER(role) LIKE LOWER(?)
                ORDER BY id
                """;

        return jdbcTemplate.query(
                sql,
                new WorkerMapper(),
                "%" + username + "%",
                "%" + role + "%"
        );
    }

    public Optional<Worker> getWorkerById(Long id) {
        var sql = """
                SELECT *
                FROM workers
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, new WorkerMapper(), id)
                .stream().findFirst();
    }

    public int addWorker(Worker worker) {
        var sql = """
                INSERT INTO workers (username, password, name, last_name, role)
                VALUES (?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                worker.username(),
                worker.password(),
                worker.name(),
                worker.lastName(),
                worker.role()
        );
    }

    public int deleteWorker(Long id) {
        var sql = """
                DELETE FROM workers
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }

    public int updateWorker(Worker worker) {
        var sql = """
                UPDATE workers
                SET username = ?, name = ?, last_name = ?, role = ?
                
                WHERE id = ?
                """;

        return jdbcTemplate.update(
                sql,
                worker.username(),
                worker.name(),
                worker.lastName(),
                worker.role(),
                worker.id()
        );
    }


    public Optional<Worker> getWorkerByUsername(String username) {
        var sql = """
                SELECT *
                FROM workers
                WHERE username = ?
                """;

        return jdbcTemplate.query(sql, new WorkerMapper(), username)
                .stream().findFirst();
    }


    public void changePassword(Long id, String encode) {
        var sql = """
                UPDATE workers
                SET password = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, encode, id);
    }
}
