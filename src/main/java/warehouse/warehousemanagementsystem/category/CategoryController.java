package warehouse.warehousemanagementsystem.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')")
    public ResponseEntity<List<Category>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> addCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
        return new ResponseEntity<>("Category added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>("Category deleted successfully", HttpStatus.OK);
    }

    @PutMapping
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> updateCategory(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return new ResponseEntity<>("Category updated successfully", HttpStatus.OK);
    }
}
