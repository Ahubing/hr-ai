package com.open.ai.eros.common.util;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;

@Component
public class FileUtils  implements ApplicationContextAware {

    @Getter
    @Value("${file-save-path}")
    private String DIC;


    @Getter
    private static volatile String outDIC;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        FileUtils bean = applicationContext.getBean(FileUtils.class);
        FileUtils.outDIC = bean.getDIC();
    }

    public File inputStreamToFile(InputStream inputStream, String FileName) throws IOException {
        File myFilePath = new File(DIC);
        if (!myFilePath.exists()) {
            myFilePath.mkdir();
        }
        File tempFile = new File(DIC, FileName);
        OutputStream outputStream = Files.newOutputStream(tempFile.toPath());
        try {
            byte buffer[] = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            return tempFile;
        } catch (Exception e) {
            e.getStackTrace();
            throw e;
        } finally {
            outputStream.close();
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
