package warehouse.warehousemanagementsystem.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse.warehousemanagementsystem.category.CategoryDto;
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

    public List<ProductDto> getProducts(String productName, Long categoryId) {
        return productDao.getProducts(productName, categoryId);
    }

    @Transactional
    public ProductDto addProduct(ProductDto product) {

        CategoryDto category = product.category();
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

    @Transactional
    public void deleteProduct(Long id) {
        Optional<ProductDto> product = productDao.getProductById(id);
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

    @Transactional
    public ProductDto updateProduct(ProductDto product) {
        CategoryDto category = product.category();
        ProductDto currentProduct = productDao.getProductById(product.id()).orElseThrow(() -> new NotFoundException("Product not found"));
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

    public List<ProductInOrderDto> getProductsByOrderHistory(Long orderHistoryId) {
        return productDao.getProductsByOrderHistory(orderHistoryId);
    }
}
