package com.open.ai.eros.file.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@Slf4j
@RestController
public class UploadController {


    @Value("${file-save-path}")
    private String fileSavePath;


    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }


    /**
     * 内部上传操作
     *
     * @param file
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/v1/file/upload")
    public String uploadFile(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        /**
         * 文件保存目录见配置文件如果目录不存在，则创建
         */
        String formatDate = formatDate(new Date(), "yyyy-MM-dd");
        String path = String.format(fileSavePath,formatDate);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //给文件重新设置一个名字
        //后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString().replaceAll("-", "")+suffix;

        //创建这个新文件
        String filePath = path + newFileName;
        //复制操作
        try {
            InputStream inputStream = file.getInputStream();

            //if(suffix.contains("jpg") || suffix.contains("jpeg") || suffix.contains("png")){
            //    changeImage(filePath,inputStream);
            //}else {
            //
            //}
            OutputStream outputStream = new FileOutputStream(filePath, false);
            try {
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
            } catch (Exception e) {
                e.getStackTrace();
                throw e;
            } finally {
                outputStream.close();
                inputStream.close();
            }
            return formatDate+"/"+newFileName;
        } catch (IOException e) {
            log.error("UploadController upload file error", e);
            return "";
        }
    }


    public  void changeImage(String filePath,InputStream inputStream) throws IOException {
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            log.error("changeImage error ",e);
            return;
        }
        int targetHeight = 1080;

        int height = originalImage.getHeight();
        int width = originalImage.getWidth();
        if(height<=targetHeight+200) {
            targetHeight = height;
        }
        int targetWidth = 680;
        if(width<targetWidth){
            targetWidth = width;
        }
        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        try {
            File file = new File(filePath);
            ImageIO.write(scaledImage, "jpg", file);
        } catch (IOException e) {
            log.error("changeImage write error ",e);
        }
    }

}
