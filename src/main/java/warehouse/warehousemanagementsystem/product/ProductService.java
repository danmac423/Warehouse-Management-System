package warehouse.warehousemanagementsystem.product;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.ConflictException;
import warehouse.warehousemanagementsystem.exception.DatabaseException;
import warehouse.warehousemanagementsystem.exception.NotFoundException;

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
            throw new ConflictException("Product with this name already exists");
        }
        if (productDao.addProduct(product) != 1) {
            throw new DatabaseException("Failed to add product");
        }
    }

    public void deleteProduct(Long id) {
        Optional<Product> product = productDao.getProductById(id);
        int result;
        if (product.isEmpty()) {
            throw new BadRequestException("Product not found");
        }
        try {
            result = productDao.deleteProduct(id);
        } catch (Exception e) {
            throw new ConflictException("This product is still in use");
        }
        if (result != 1) {
            throw new DatabaseException("Failed to delete product");
        }
    }

    public void updateProduct(Product product) {
        Product currentProduct = productDao.getProductById(product.id()).orElseThrow(() -> new NotFoundException("Product not found"));
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
        if (productDao.getProductByName(product).isPresent() && !currentProduct.name().startsWith(product.name())) {
            throw new ConflictException("Product with this name already exists");
        }
        if (productDao.updateProduct(product) != 1) {
            throw new DatabaseException("Failed to update product");
        }
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productDao.getProductsByCategory(categoryId);
    }

    public List<ProductInOrder> getProductsByOrder(Long orderId) {
        return productDao.getProductsByOrder(orderId);
    }
}
