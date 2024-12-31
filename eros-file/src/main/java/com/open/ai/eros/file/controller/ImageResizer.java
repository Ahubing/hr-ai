package com.open.ai.eros.file.controller;

/**
 * @类名：ImageResizer
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/22 12:56
 */
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageResizer {
    public static void main(String[] args) {
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(new File("C:\\Users\\陈臣\\Desktop\\1\\c61937a2a0d73acd8d1cd759a5872d5.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int targetWidth = 800;
        int targetHeight = 2000;

        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        try {
            ImageIO.write(scaledImage, "jpg", new File("C:\\Users\\陈臣\\Desktop\\1\\c61937a2a0d73acd8d1cd759a5872d5-test.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
