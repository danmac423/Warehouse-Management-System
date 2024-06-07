package warehouse.warehousemanagementsystem.product;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> getAllProducts() {
        return productDao.getAllProducts();
    }

    public void addProduct(Product product) {
        if (product.name().isEmpty()
            || product.price() == null
            || product.categoryId() == null) {
            throw new BadRequestException("All fields are required");
        }
        if (product.stock() < 0) {
            throw new BadRequestException("Stock cannot be negative");
        }
        if (product.price().compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new BadRequestException("Price cannot be negative");
        }
        if (productDao.getProductByName(product).isPresent()) {
            throw new BadRequestException("Product already exists");
        }
        if (productDao.addProduct(product) != 1) {
            throw new BadRequestException("Failed to add product");
        }
    }

    public void deleteProduct(Long id) {
        Optional<Product> product = productDao.getProductById(id);
        int result;
        if (product.isEmpty()) {
            throw new IllegalArgumentException("Product not found");
        }
        try {
            result = productDao.deleteProduct(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("This product is still in use");
        }
        if (result != 1) {
            throw new IllegalArgumentException("Failed to delete product");
        }
    }

    public void updateProduct(Product product) {
        if (productDao.getProductById(product.id()).isEmpty()) {
            throw new BadRequestException("Product not found");
        }
        if (productDao.getProductByName(product).isPresent()) {
            throw new BadRequestException("Product already exists");
        }
        if (product.name().isEmpty()
                || product.price() == null
                || product.categoryId() == null) {
            throw new BadRequestException("All fields are required");
        }
        if (product.stock() < 0) {
            throw new BadRequestException("Stock cannot be negative");
        }
        if (product.price().compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new BadRequestException("Price cannot be negative");
        }
        if (productDao.updateProduct(product) != 1) {
            throw new BadRequestException("Failed to update product");
        }
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productDao.getProductsByCategory(categoryId);
    }
}
