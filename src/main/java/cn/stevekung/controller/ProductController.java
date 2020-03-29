package cn.stevekung.controller;

import cn.stevekung.pojo.Product;
import cn.stevekung.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jpa")
public class ProductController {
    @Autowired
    ProductServiceImpl productService;
    @GetMapping("/getAllProducts")
    List<Product> getAllProducts(){
        return productService.getAllProducts();
    }
    @GetMapping("/getProductById/{id}")
    Product getProductById(@PathVariable("id") Integer id){
        return productService.getProductById(id);
    }
    @GetMapping("/deleteProductById/{id}")
    void deleteProductById(@PathVariable("id") Integer id){
        productService.deleteProductById(id);
    }
    @GetMapping("/addProduct")
    void addProduct(){
        Product product = new Product("戴尔", 6000);
        productService.addProduct(product);
    }
    @GetMapping("/updateProduct") // 问号传参
    void updateProduct(Product product){
        productService.updateProduct(product);
    }
}
