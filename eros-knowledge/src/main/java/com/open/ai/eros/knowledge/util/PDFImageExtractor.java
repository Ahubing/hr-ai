package com.open.ai.eros.knowledge.util;

import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.util.FileUtils;
import com.open.ai.eros.common.util.HttpFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


@Component
@Slf4j
public class PDFImageExtractor{


    private static String saveImage(PDImageXObject image, String fileName) throws IOException {
        String outputPath = FileUtils.getOutDIC() + fileName;
        File outputFile = new File(outputPath);
        boolean png = ImageIO.write(image.getImage(), "PNG", outputFile);
        if(png){
            String upload = HttpFileUtil.upload(outputFile);
            if(StringUtils.isNoneEmpty(upload)){
                return "http://43.153.41.128/"+upload;
            }
        }
        return "";
    }



    public static String getPdfText(String localFilePath) {

        if(!localFilePath.endsWith(".pdf")){
            throw new BizException("不是pdf文件");
        }

        try (PDDocument document = PDDocument.load(new File(localFilePath))) {
            StringBuilder textWithImagePaths = new StringBuilder();

            PDFTextStripper stripper = new PDFTextStripper();

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                String pageText = stripper.getText(document);
                textWithImagePaths.append(pageText);
            }
            return textWithImagePaths.toString();
        } catch (IOException e) {
            log.error("getPdfImageAndText localFilePath={}",e,e);
        }
        return null;
    }


    public static String getPdfImageAndText(String localFilePath) {

        if(!localFilePath.endsWith(".pdf")){
            throw new BizException("不是pdf文件");
        }

        try (PDDocument document = PDDocument.load(new File(localFilePath))) {
            StringBuilder textWithImagePaths = new StringBuilder();

            PDFTextStripper stripper = new PDFTextStripper();

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                String pageText = stripper.getText(document);

                PDPage page = document.getPage(i);
                PDResources resources = page.getResources();
                for (COSName xObjectName : resources.getXObjectNames()) {
                    PDXObject xObject = resources.getXObject(xObjectName);
                    if (xObject instanceof PDImageXObject) {
                        PDImageXObject image = (PDImageXObject) xObject;
                        String imageName = "image_" + i + "_" + xObjectName.getName() + ".png";
                        String imagePath = saveImage(image, imageName);
                        if(StringUtils.isEmpty(imagePath)){
                            continue;
                        }
                        // 在文本中插入图片路径
                        String imageMarker = "![IMAGE]:(" + imagePath + ")";
                        pageText = pageText.replaceFirst("\\S+", imageMarker);
                    }
                }

                textWithImagePaths.append(pageText);
            }

            // 保存带有图片路径的文本
            //try (PrintWriter out = new PrintWriter("C:\\Users\\陈臣\\Desktop\\output.txt")) {
            //    out.println(textWithImagePaths.toString());
            //}
            return textWithImagePaths.toString();
        } catch (IOException e) {
            log.error("getPdfImageAndText localFilePath={}",e,e);
        }
        return null;
    }



    public static void main(String[] args) {

        getPdfImageAndText("");
    }


}
