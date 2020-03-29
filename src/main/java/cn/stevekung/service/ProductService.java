package cn.stevekung.service;

import cn.stevekung.pojo.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Integer id);
    void deleteProductById(Integer id);
    Product addProduct(Product product);
    Product updateProduct(Product product);
}
