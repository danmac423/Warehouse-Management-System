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

    public List<Worker> getAllWorkers() {
        var sql = """
                SELECT * FROM workers
                ORDER BY id
                """;

        return jdbcTemplate.query(
                sql,
                new WorkerMapper()
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
                SET username = ?, password = ?, name = ?, last_name = ?, role = ?
                WHERE id = ?
                """;

        return jdbcTemplate.update(
                sql,
                worker.username(),
                worker.password(),
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


}
