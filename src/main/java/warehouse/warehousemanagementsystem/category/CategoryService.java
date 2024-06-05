package warehouse.warehousemanagementsystem.category;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {


    private final CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public List<Category> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public Category getCategoryById(Long id) {
        return categoryDao.getCategoryById(id);
    }

    public void addCategory(Category category) {
        categoryDao.addCategory(category);
    }

    public void deleteCategory(Long id) {
        categoryDao.deleteCategory(id);
    }


}
