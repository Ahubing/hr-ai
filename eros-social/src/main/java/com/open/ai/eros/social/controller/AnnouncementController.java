package com.open.ai.eros.social.controller;


import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.social.bean.req.AddAnnouncementReq;
import com.open.ai.eros.social.bean.req.SearchAnnouncementReq;
import com.open.ai.eros.social.bean.req.UpdateAnnouncementReq;
import com.open.ai.eros.social.bean.vo.AnnouncementVo;
import com.open.ai.eros.social.config.SocialBaseController;
import com.open.ai.eros.social.manager.AnnouncementManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 公告公共表 前端控制器
 * </p>
 *
 */
@Api(tags = "公告控制类")
@RestController
public class AnnouncementController extends SocialBaseController {


    @Resource
    private AnnouncementManager announcementManager;

    /**
     * 新增公告
     */
    @ApiOperation("新增公告")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/announcement/add")
    public ResultVO addAnnouncement(@RequestBody @Valid AddAnnouncementReq addAnnouncementReq) {
        // 获取当前登录的账号
        String account = getAccount();
        // 新增公告
        int result = announcementManager.addAnnouncementConfig(addAnnouncementReq, account);
        // 判断是否新增成功
        if (result > 0) {
            return ResultVO.success("新增成功");
        }
        return ResultVO.fail("新增失败");
    }

    /**
     * c端查询公告
     */
    @ApiOperation("c端查询公告")
    @GetMapping("/announcement/list")
    public ResultVO<List<AnnouncementVo>> listAnnouncement() {
        List<AnnouncementVo> allAnnouncementConfig = announcementManager.getAllAnnouncementConfig();
        return ResultVO.success(allAnnouncementConfig);
    }


    /**
     * c端查询公告
     */
    @ApiOperation("c端最新的查询公告")
    @GetMapping("/announcement/new")
    public ResultVO<AnnouncementVo> bestAnnouncement() {
        return announcementManager.getBestAnnouncementVo();
    }


    /**
     * 删除公告
     */
    @ApiOperation("删除公告")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @GetMapping("/announcement/delete")
    public ResultVO deleteAnnouncement(@RequestParam(value = "id") Long id) {
        // 调用方法进行删除
        return announcementManager.deleteAnnouncement(id);
    }

    /**
     * 修改公告
     */
    @ApiOperation("修改公告")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/announcement/update")
    public ResultVO updateAnnouncement(@RequestBody @Valid UpdateAnnouncementReq addAnnouncementReq) {
        // 修改公告
        int result = announcementManager.updateAnnouncement(addAnnouncementReq,getAccount());

        // 判断是否修改成功
        if (result > 0) {
            return ResultVO.success("修改成功");
        }
        return ResultVO.fail("修改失败");
    }


    /**
     * 查询公告
     */
    @ApiOperation("查询公告")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/announcement/query")
    public ResultVO<PageVO<AnnouncementVo> > listAnnouncement(@RequestBody @Valid SearchAnnouncementReq req) {
        if(req.getPageNum()<=0){
            req.setPageNum(1);
        }
        if(req.getPageSize()<=0){
            req.setPageSize(10);
        }
        PageVO<AnnouncementVo> announcementConfig = announcementManager.getAnnouncementConfig(req);
        return ResultVO.success(announcementConfig);
    }

}

