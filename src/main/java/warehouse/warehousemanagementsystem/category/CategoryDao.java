package warehouse.warehousemanagementsystem.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Category> getAllCategories() {
        var sql = """
                SELECT c.id, c.name, COUNT(p.id) AS productCount
                FROM categories c LEFT JOIN products p ON c.id = p.category_id
                GROUP BY c.id, c.name
                ORDER BY c.id
                """;
        return jdbcTemplate.query(
                sql,
                new CategoryMapper()
        );
    }

    public List<Category> getCategoriesBySubstring(String substring) {
        var sql = """
                SELECT c.id, c.name, COUNT(p.id) AS productCount
                FROM categories c LEFT JOIN products p ON c.id = p.category_id
                WHERE LOWER(c.name) LIKE LOWER(?)
                GROUP BY c.id, c.name
                ORDER BY c.id
                """;
        return jdbcTemplate.query(
                sql,
                new CategoryMapper(),
                "%" + substring + "%"
        );
    }

    public Optional<Category> getCategoryById(Long id) {
        var sql = """
                SELECT c.id, c.name, COUNT(p.id) AS productCount
                FROM categories c LEFT JOIN products p ON c.id = p.category_id
                WHERE c.id = ?
                GROUP BY c.id, c.name
                """;
        return jdbcTemplate.query(sql, new CategoryMapper(), id)
                .stream().findFirst();
    }

    public Optional<Category> getCategoryByName(String name) {
        var sql = """
                SELECT c.id, c.name, COUNT(p.id) AS productCount
                FROM categories c LEFT JOIN products p ON c.id = p.category_id
                WHERE c.name = ?
                GROUP BY c.id, c.name
                """;
        return jdbcTemplate.query(sql, new CategoryMapper(), name)
                .stream().findFirst();
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

    public int updateCategory(Category category) {
        var sql = """
                UPDATE categories
                SET name = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                sql,
                category.name(),
                category.id()
        );
    }
}