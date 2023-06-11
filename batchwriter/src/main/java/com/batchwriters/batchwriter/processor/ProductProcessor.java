package com.batchwriters.batchwriter.processor;

import com.batchwriters.batchwriter.model.Product;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ProductProcessor implements ItemProcessor<Product,Product> {
    @Override
    public Product process(Product o) throws Exception {
         //o.setProductDesc(o.getProductDesc() + "nish was here");
         //return o;
        //if (o.getProductId() == 2){
        //    throw new RuntimeException("Because ID is 2");
        //}else{
            o.setProductDesc(o.getProductDesc() + "nish was here");
        //}
        return o;
    }
}
