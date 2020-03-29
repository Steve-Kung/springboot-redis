package cn.stevekung.service;

import cn.stevekung.dao.ProductRepository;
import cn.stevekung.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "productCache")
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Override // 放入productCache缓存中, key 为方法名， 值为函数返回值
    @Cacheable(key = "#root.method.name") // 缓存查询
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override // 放入productCache缓存中, key为 product[id]， 值为函数返回值
    @Cacheable(key = "'product['+ #root.args[0] +']'") // 缓存查询
    public Product getProductById(Integer id) {
        return productRepository.findById(id).get();
    }

    @Override // 在方法执行之前的时候就清空缓存
    @CacheEvict(key = "'product['+ #root.args[0] +']'", beforeInvocation = true) // 缓存清除
    public void deleteProductById(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    @CachePut(key = "'product['+ #root.args[0].id +']'") // 缓存更新
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @CachePut(key = "'product['+ #root.args[0].id +']'") // 缓存更新
    public Product updateProduct(Product product) {
        // 根据product的主键是否为零 决定是insert还是update
        return productRepository.save(product);
    }
}
