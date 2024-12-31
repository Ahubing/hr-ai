package com.open.ai.eros.common.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @类名：HttpFileUtil
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/18 20:17
 */

@Component
@Slf4j
public class HttpFileUtil implements ApplicationContextAware {


    private static volatile FileUtils fileUtils;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        fileUtils = applicationContext.getBean(FileUtils.class);
    }

    public static File downloadFile(String fileUrl) throws IOException {
        // 全是OKhttp中的类定义
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(fileUrl)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        assert response.body() != null;
        InputStream inputStream = response.body().byteStream();
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        log.info("开始下载网络文件  fileUrl={}",fileUrl);
        File file = fileUtils.inputStreamToFile(inputStream, fileName);
        log.info("下载网络文件结束  fileUrl={} fileName={}",fileUrl,file.getName());
        return file;
    }



    public static void main(String[] args) throws IOException {
        File file = downloadFile("http://43.153.41.128/2024-09-18/52e3aaa9a7174530b8080e84cc2e7644.pdf");
        System.out.println(file.getName());
    }


    public static String upload(File file) throws IOException {
        // url接口路径
        String url = "http://43.153.41.128/v1/file/upload";
        // file是要上传的文件 File()  这边我上传的是excel，其他类型可以自己改这个parse
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        //这边是把file写进来，也有写路径的，但我这边是写file文件，parse不行的话可以直接改这个"multipart/form-data"
        // 创建OkHttpClient实例,设置超时时间
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60L, TimeUnit.SECONDS)
                .writeTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .build();
        // 不仅可以支持传文件，还可以在传文件的同时，传参数
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM) // 设置传参为form-data格式
                .addFormDataPart("file", file.getName(), fileBody) // 中间参数为文件名
                .build();

        // 构建request请求体，有需要传请求头自己加
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        String result = null;
        try {
            // 发送请求
            response = okHttpClient.newCall(request).execute();
            result = response.body().string();
            log.info(url + "发送请求结果:" + result);
            if (!response.isSuccessful()) {
                log.info("请求失败");
                return null;
            }
            response.body().close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        // 会在本地产生临时文件，用完后需要删除
        if (file.exists()) {
            file.delete();
        }
        return result;
    }


    /**
     * 使用上传文件
     * @param mFile
     * @return
     * @throws IOException
     */
    public static   String upload(MultipartFile mFile) throws IOException {
        // 这里是MultipartFile转File的过程
        File file = fileUtils.inputStreamToFile(mFile.getInputStream(), Objects.requireNonNull(mFile.getOriginalFilename()));
        return upload(file);
    }

}
