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
        var categories =  categoryDao.getAllCategories();
        if (categories.isEmpty()) {
            throw new NotFoundException("No categories found");
        }
        return categories;
    }

    public List<Category> getCategoriesByPrefixSuffix(String prefixSuffix) {
        var categories = categoryDao.getCategoriesByPrefixSuffix(prefixSuffix);
        if (categories.isEmpty()) {
            throw new NotFoundException("No categories found");
        }
        return categories;
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
        Category currentCategory = categoryDao.getCategoryById(category.id()).orElseThrow(() -> new NotFoundException("Category not found"));

        if (category.name().isEmpty()) {
            throw new BadRequestException("Category name cannot be empty");
        }
        if (categoryDao.getCategoryByName(category.name()).isPresent() && !currentCategory.name().startsWith(category.name())) {
            throw new ConflictException("Category already exists");
        }
        if (categoryDao.updateCategory(category) != 1) {
            throw new DatabaseException("Failed to update category");
        }
    }

}
