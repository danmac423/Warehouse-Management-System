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
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts());
    }

    @GetMapping("/categoryId/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/productName/{productName}")
    public List<Product> getProductsByProductCame(@PathVariable String productName) {
        return productService.getProductsByProductName(productName);
    }

    @GetMapping("/categoryId/{categoryId}/productName/{productName}")
    public List<Product> getProductsByCategoryIdAndProductName(@PathVariable Long categoryId, @PathVariable String productName) {
        return productService.getProductsByCategoryIdAndProductName(categoryId, productName);
    }



    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product newProduct = productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
    }

    @PutMapping
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

//    @GetMapping("/order/{orderId}")
//    public ResponseEntity<List<ProductInOrder>> getProductsByOrder(@PathVariable Long orderId) {
//        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductsByOrder(orderId));
//    }
//
//    @GetMapping("/orderHistory/{orderId}")
//    public ResponseEntity<List<ProductInOrder>> getProductsByOrderHistory(@PathVariable Long orderId) {
//        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductsByOrderHistory(orderId));
//    }


}
