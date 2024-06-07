package warehouse.warehousemanagementsystem.product;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> getAllProducts() {
        return productDao.getAllProducts();
    }

    public Product getProductById(Long id) {
        return productDao.getProductById(id);
    }

    public int addProduct(Product product) {
        return productDao.addProduct(product);
    }

    public int deleteProduct(Long id) {
        return productDao.deleteProduct(id);
    }

    public int updateProduct(Product product) {
        return productDao.updateProduct(product);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productDao.getProductsByCategory(categoryId);
    }
}
