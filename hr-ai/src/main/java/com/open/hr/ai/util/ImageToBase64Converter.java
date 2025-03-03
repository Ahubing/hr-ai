package com.open.hr.ai.util;

import java.io.*;
import java.nio.file.*;
import java.util.Base64;

public class ImageToBase64Converter {

    /**
     * 将图片文件转换为 Base64 字符串
     *
     * @param imagePath 图片文件路径
     * @return Base64 编码的字符串
     * @throws IOException 如果文件读取失败
     */
    public static String convertImageToBase64(String imagePath) throws IOException {
        // 读取文件字节
        byte[] fileContent = Files.readAllBytes(Paths.get(imagePath));
        // 将字节数组编码为 Base64
        return Base64.getEncoder().encodeToString(fileContent);
    }

    /**
     * 遍历文件夹中的所有图片文件，并将其转换为 Base64
     *
     * @param folderPath 文件夹路径
     * @throws IOException 如果文件夹读取失败
     */
    public static void processFolder(String folderPath) throws IOException {
        File folder = new File(folderPath);

        if (!folder.isDirectory()) {
            System.err.println("指定路径不是一个文件夹！");
            return;
        }

        // 列出文件夹中的所有文件
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("文件夹中没有找到任何文件！");
            return;
        }

        for (File file : files) {
            // 检查文件是否是图片
            if (file.isFile() && isImageFile(file)) {
                System.out.println("正在处理文件: " + file.getName());
                String base64 = convertImageToBase64(file.getAbsolutePath());
                System.out.println("Base64 编码: ");
                System.out.println(base64);
                System.out.println("====================================");
            }
        }
    }

    /**
     * 判断文件是否是图片文件
     *
     * @param file 文件对象
     * @return 如果是图片文件则返回 true，否则返回 false
     */
    public static boolean isImageFile(File file) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".bmp", ".gif"};
        String fileName = file.getName().toLowerCase();
        for (String ext : imageExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        // 提示用户输入文件夹路径
        try {
            processFolder("/Users/linyous/Downloads/test3");
        } catch (IOException e) {
            System.err.println("读取文件夹路径失败: " + e.getMessage());
        }
    }
}
