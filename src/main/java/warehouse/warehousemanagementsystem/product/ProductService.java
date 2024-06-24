package warehouse.warehousemanagementsystem.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.category.Category;
import warehouse.warehousemanagementsystem.category.CategoryDao;
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
    private final CategoryDao categoryDao;

    @Autowired
    public ProductService(ProductDao productDao, CategoryDao categoryDao) {
        this.productDao = productDao;
        this.categoryDao = categoryDao;
    }

    public List<Product> getProducts(String productName, Long categoryId) {
        return productDao.getProducts(productName, categoryId);
    }


    public Product addProduct(Product product) {

        Category category = product.category();
        if (category == null) {
            throw new BadRequestException("Category is required");
        }

        if (product.name().isEmpty()
            || product.price() == null
            || category.id() == null) {
            throw new BadRequestException("All fields are required");
        }

        if (categoryDao.getCategoryById(category.id()).isEmpty()) {
            throw new NotFoundException("Category not found");
        }

        if (product.stock() < 0) {
            throw new BadRequestException("Stock cannot be negative");
        }
        if (product.price().compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new BadRequestException("Price cannot be negative");
        }
        if (productDao.getProductByName(product.name()).isPresent()) {
            throw new ConflictException("Product with this name already exists");
        }

        return productDao.addProduct(product);
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

    public Product updateProduct(Product product) {
        Category category = product.category();
        Product currentProduct = productDao.getProductById(product.id()).orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.name().isEmpty()
                || product.price() == null
                || product.stock() == 0
                || category.id() == null) {
            throw new BadRequestException("All fields are required");
        }
        if (categoryDao.getCategoryById(category.id()).isEmpty()) {
            throw new NotFoundException("Category not found");
        }
        if (product.stock() < 0) {
            throw new BadRequestException("Stock cannot be negative");
        }
        if (product.price().compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new BadRequestException("Price cannot be negative");
        }
        if (productDao.getProductByName(product.name()).isPresent() && !currentProduct.name().startsWith(product.name())) {
            throw new ConflictException("Product with this name already exists");
        }
        return productDao.updateProduct(product);
    }

    public List<ProductInOrder> getProductsByOrderHistory(Long orderHistoryId) {
        return productDao.getProductsByOrderHistory(orderHistoryId);
    }
//
//    public List<ProductInOrder> getProductsByOrder(Long orderId) {
//        return productDao.getProductsByOrder(orderId);
//    }
//
//    public List<ProductInOrder> getProductsByOrderHistory(Long orderId) { return productDao.getProductsByOrderHistory(orderId); }
}
