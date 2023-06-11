package com.batchwriters.batchwriter.processor;

import com.batchwriters.batchwriter.model.Product;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ProductProcessor implements ItemProcessor<Product,Product> {
    @Override
    public Product process(Product o) throws Exception {
         o.setProductDesc(o.getProductDesc() + "nish was here");
         return o;
    }
}
