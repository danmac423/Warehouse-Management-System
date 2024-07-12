package warehouse.warehousemanagementsystem.worker;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkerMapper implements RowMapper<WorkerDto> {

    @Override
    public WorkerDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new WorkerDto(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("last_name"),
                rs.getString("role")
        );
    }
}
