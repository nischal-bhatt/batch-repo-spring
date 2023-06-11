package com.lab.reader;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;

import java.util.Arrays;
import java.util.List;

public class InMemeReader extends AbstractItemStreamItemReader {

    Integer[] intArr = {1,2,3,4,5,6,7,8,9,10};
    List<Integer> myList = Arrays.asList(intArr);

    int index = 0;

    @Override
    public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Integer nextItem = null;
        if (index < myList.size()){
            nextItem = myList.get(index);
            index++;
        }else{
            index = 0;
        }
        return nextItem;
    }
}
