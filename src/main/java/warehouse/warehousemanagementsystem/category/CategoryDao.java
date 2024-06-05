package warehouse.warehousemanagementsystem.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Category> getAllCategories() {
        var sql = """
                SELECT * FROM categories
                """;
        return jdbcTemplate.query(
                sql,
                new CategoryMapper()
        );
    }

    public Category getCategoryById(Long id) {
        var sql = """
                SELECT * FROM categories
                WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(
                sql,
                new CategoryMapper(),
                id
        );
    }

    public int addCategory(Category category) {
        var sql = """
                INSERT INTO categories (name)
                VALUES (?)
                """;
        return jdbcTemplate.update(
                sql,
                category.name()
        );
    }

    public int deleteCategory(Long id) {
        var sql = """
                DELETE FROM categories
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                id
        );
    }
}