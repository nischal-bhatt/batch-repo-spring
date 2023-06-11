package com.swt.simpleproductservice.controller;

import com.swt.simpleproductservice.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class ProductController {

    @GetMapping("/product")
    public Product getProduct(){

        return new Product(2,"test","test", BigDecimal.valueOf(2.33),2);
    }
}
