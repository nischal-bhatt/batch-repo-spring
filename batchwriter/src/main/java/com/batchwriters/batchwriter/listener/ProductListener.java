package com.batchwriters.batchwriter.listener;

import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.item.file.FlatFileParseException;

import java.io.FileOutputStream;
import java.io.IOException;

public class ProductListener {

    private String fileName="error/read_skipped";
    private String fileName2="error/process_skipped";

    @OnSkipInRead
    public void onSkipRead(Throwable t){
       if(t instanceof FlatFileParseException){
           FlatFileParseException f = (FlatFileParseException)t;
           onSkip(f.getInput(),fileName);
       }
    }

    @OnSkipInProcess
    public void skipInProcess(Object item, Throwable t){
        if(t instanceof RuntimeException){

            onSkip(item,fileName2);
        }
    }

    public void onSkip(Object s, String fileName){
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(fileName, true);

            fos.write(s.toString().getBytes());
            fos.write("\r\n".getBytes());
            fos.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}
