package com.batchwriters.batchwriter.service;

import com.batchwriters.batchwriter.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductService {

    public Product getProduct(){
        RestTemplate restTemplate =
                new RestTemplate();
        String url = "http://localhost:8080/product";
        Product s = restTemplate.getForObject(url,Product.class);
        return s;
    }
}
