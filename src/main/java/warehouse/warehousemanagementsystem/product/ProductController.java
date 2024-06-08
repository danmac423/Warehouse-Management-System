package warehouse.warehousemanagementsystem.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    public void addProduct(@RequestBody Product product) {
        productService.addProduct(product);
    }

    @DeleteMapping("{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @PutMapping
    public void updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
    }

//    @GetMapping("/category/{categoryId}")
//    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
//        return productService.getProductsByCategory(categoryId);
//    }
}
