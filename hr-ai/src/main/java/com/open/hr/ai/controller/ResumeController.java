package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.hr.ai.bean.req.AddAmResumeParseReq;
import com.open.hr.ai.bean.req.AmUploadResumeSearchReq;
import com.open.hr.ai.bean.req.SearchAmResumeReq;
import com.open.hr.ai.bean.req.UploadAmResumeUpdateReq;
import com.open.hr.ai.bean.vo.AmResumeCountDataVo;
import com.open.hr.ai.bean.vo.AmResumeVo;
import com.open.hr.ai.bean.vo.UploadAmResumeVo;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.ResumeManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Date 2025/1/4 23:19
 */

@Api(tags = "resume管理类")
@Slf4j
@RestController
public class ResumeController extends HrAIBaseController {


    @Resource
    private ResumeManager resumeManager;


    @Value("${file-save-path}")
    private String fileSavePath;



    @Value("${domain-name}")
    private String domainName;


    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }



    @ApiOperation("获取简历列表")
    @VerifyUserToken
    @GetMapping("resume/list")
    public ResultVO<PageVO<AmResume>> promptList(@RequestParam(value = "type", required = true) Integer type, @RequestParam(value = "post_id", required = false) Integer post_id, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "page", required = true) Integer page, @RequestParam(value = "size", required = true) Integer size) {
        return resumeManager.resumeList(getUserId(), type, post_id, name, page, size);
    }

    @ApiOperation("统计简历数据")
    @VerifyUserToken
    @GetMapping("resume/data")
    public ResultVO<List<AmResumeCountDataVo>> promptData() {
        return resumeManager.resumeData(getUserId());
    }

    @ApiOperation("获取简历详情")
    @VerifyUserToken
    @GetMapping("resume/detail")
    public ResultVO<AmResumeVo> promptDetail(@RequestParam(value = "id", required = true) Integer id) {
        return resumeManager.resumeDetail(id);
    }


    /**
     * todo 待补充php 高级的智能匹配...(php没写)
     *
     * @param searchAmResumeReq
     * @return
     */
    @ApiOperation("智能匹配")
    @VerifyUserToken
    @PostMapping("resume/search")
    public ResultVO resumeSearch(@RequestBody @Valid SearchAmResumeReq searchAmResumeReq) {
        if (Objects.isNull(searchAmResumeReq)) {
            return ResultVO.fail("参数不能为空");
        }

       return resumeManager.resumeSearch(searchAmResumeReq,getUserId());
    }


    /**
     * 简历文件上传操作
     *
     * @param files
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/resume/upload")
    public ResultVO<String> uploadFiles(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
            String formatDate = formatDate(new Date(), "yyyy-MM-dd");
            String path = String.format(fileSavePath, formatDate);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;

            String filePath = path + newFileName;

            try {
                InputStream inputStream = file.getInputStream();
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
                String fileUrl = domainName + formatDate + "/" + newFileName;
                log.info("UploadController upload file success, fileUrl:{}", fileUrl);
                return ResultVO.success(fileUrl);
            } catch (IOException e) {
                log.error("UploadController upload file error", e);
            }
        return ResultVO.fail("上传失败");
    }

    /**
     * 简历文件解析
     * @return
     */
    @ApiOperation("自定义上传简历解析")
    @VerifyUserToken
    @PostMapping("upload/resume/parse")
    public ResultVO parseResume(@RequestBody @Valid AddAmResumeParseReq addAmResumeParseReq) {
        if (Objects.isNull(addAmResumeParseReq)) {
            return ResultVO.fail("参数不能为空");
        }
        return resumeManager.resumeAnalysis(addAmResumeParseReq, getUserId());
    }

    /**
     * 简历文件解析
     * @return
     */
    @ApiOperation("自定义上传简历修改")
    @VerifyUserToken
    @PostMapping("upload/resume/update")
    public ResultVO uploadResumeUpdate(@RequestBody @Valid UploadAmResumeUpdateReq uploadAmResumeUpdateReq) {
        if (Objects.isNull(uploadAmResumeUpdateReq)) {
            return ResultVO.fail("参数不能为空");
        }
        return resumeManager.updateUploadAmResume(uploadAmResumeUpdateReq, getUserId());
    }


    /**
     * 查询用户自定义上传的简历列表
     * @return
     */
    @ApiOperation("查询用户自定义上传的简历列表")
    @VerifyUserToken
    @PostMapping("upload/resume/search")
    public ResultVO<PageVO<UploadAmResumeVo>> uploadResumeSearch(@RequestBody @Valid AmUploadResumeSearchReq req) {
        return resumeManager.UploadAmResumeSearch(req,getUserId());
    }

}
