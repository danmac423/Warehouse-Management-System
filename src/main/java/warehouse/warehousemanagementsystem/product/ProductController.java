package warehouse.warehousemanagementsystem.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @GetMapping("/prefixSuffix/{prefixSuffix}")
    public List<Product> getProductsByPrefixSuffix(@PathVariable String prefixSuffix) {
        return productService.getProductsByPrefixSuffix(prefixSuffix);
    }

    @GetMapping("/category/{categoryId}/prefixSuffix/{prefixSuffix}")
    public List<Product> getProductsByCategoryAndPrefixSuffix(@PathVariable Long categoryId, @PathVariable String prefixSuffix) {
        return productService.getProductsByCategoryAndPrefixSuffix(categoryId, prefixSuffix);
    }



    @PostMapping
    public ResponseEntity<String> addProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return new ResponseEntity<>("Product added successfully", HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
        return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ProductInOrder>> getProductsByOrder(@PathVariable Long orderId) {
        return new ResponseEntity<>(productService.getProductsByOrder(orderId), HttpStatus.OK);
    }


}
