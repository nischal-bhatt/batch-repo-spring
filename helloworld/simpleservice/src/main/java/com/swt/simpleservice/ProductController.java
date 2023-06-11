package com.swt.simpleservice;

import com.swt.simpleservice.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {

    @GetMapping("/products")
    public List<Product> getProducts(){

        ArrayList<Product> products
                = new ArrayList<>();

        products.add(new Product(1,"fromrest","test", BigDecimal.valueOf(2.33),10));
        products.add(new Product(2,"fromrest","test",BigDecimal.valueOf(2.33),10));
        return products;
    }
}
