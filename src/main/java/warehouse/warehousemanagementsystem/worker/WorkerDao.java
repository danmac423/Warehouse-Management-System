package warehouse.warehousemanagementsystem.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class WorkerDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public WorkerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int addWorker(Worker worker) {
        var sql = """
                INSERT INTO workers (username, password, name, last_name, role)
                VALUES (?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(sql, worker.username(), worker.password(), worker.name(), worker.lastName(), worker.role());
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
