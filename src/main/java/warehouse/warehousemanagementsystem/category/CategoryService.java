package warehouse.warehousemanagementsystem.category;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.ConflictException;
import warehouse.warehousemanagementsystem.exception.DatabaseException;
import warehouse.warehousemanagementsystem.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {


    private final CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public List<Category> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public void addCategory(Category category) {
        if (categoryDao.getCategoryByName(category.name()).isPresent()) {
            throw new ConflictException("Category already exists");
        }
        if (category.name().isEmpty()) {
            throw new BadRequestException("Category name cannot be empty");
        }
        if (categoryDao.addCategory(category) != 1) {
            throw new DatabaseException("Failed to add category");
        }
    }

    public void deleteCategory(Long id) {
        Optional<Category> category = categoryDao.getCategoryById(id);
        int result;
        if (category.isEmpty()) {
            throw new NotFoundException("Category not found");
        }
        try {
            result = categoryDao.deleteCategory(id);
        } catch (Exception e) {
            throw new ConflictException("Some products are still using this category");
        }

        if (result != 1) {
            throw new DatabaseException("Failed to delete category");
        }
    }

    public void updateCategory(Category category) {
        if (categoryDao.getCategoryById(category.id()).isEmpty()) {
            throw new NotFoundException("Category not found");
        }
        if (category.name().isEmpty()) {
            throw new BadRequestException("Category name cannot be empty");
        }
        if (categoryDao.updateCategory(category) != 1) {
            throw new DatabaseException("Failed to update category");
        }
    }

}
