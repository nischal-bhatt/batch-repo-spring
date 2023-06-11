package com.batchwriters.batchwriter.reader;

import com.batchwriters.batchwriter.model.Product;
import com.batchwriters.batchwriter.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceAdapter {

    Logger logger = LoggerFactory.getLogger(ProductServiceAdapter.class);

    @Autowired
    ProductService productService;

    public Product nextProduct() throws InterruptedException {
        Product s = null;
        try {
            s = productService.getProduct();
            logger.info("connected to web service.... ok");
        }catch (Exception e){
            logger.info("exception occured..............");
            throw e;
        }
        Thread.sleep(1000);
        return s;
    }
}
