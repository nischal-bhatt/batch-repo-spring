package com.lab.service;

import com.lab.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    public ArrayList<Product> getProducts(){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/products";
        Product[] products = restTemplate.getForObject(url,Product[].class);

        ArrayList<Product> prods = new ArrayList<>();
        for (Product p : products)
        {
            prods.add(p);
        }

        return prods;
    }
}
