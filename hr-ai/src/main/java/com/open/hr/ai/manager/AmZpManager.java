package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmAdmin;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.entity.AmZpPlatforms;
import com.open.ai.eros.db.mysql.hr.service.impl.AmAdminServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmZpLocalAccoutsServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmZpPlatformsServiceImpl;
import com.open.hr.ai.bean.AmZpAccoutsResultVo;
import com.open.hr.ai.bean.AmZpPlatformsResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class AmZpManager {

    @Resource
    private AmZpLocalAccoutsServiceImpl amZpLocalAccoutsService;

    @Resource
    private AmZpPlatformsServiceImpl amZpPlatformsService;

    @Resource
    private AmAdminServiceImpl amAdminService;

    public ResultVO<AmZpAccoutsResultVo> getAccouts(Long id) {
        AmZpAccoutsResultVo resultVo = new AmZpAccoutsResultVo();
        int account_online_num = 0;
        int account_running_num = 0;
        String[] citys = {"北京", "上海", "深圳", "广州", "杭州", "成都"};

        List<AmZpLocalAccouts> list = amZpLocalAccoutsService.getList(id);
        List<AmZpPlatforms> platforms = amZpPlatformsService.list();
        for (AmZpLocalAccouts accouts : list) {
            if ("active".equals(accouts.getState())) {
                account_online_num++;
            }
            if (accouts.getIsRunning() == 1) {
                account_running_num++;
            }
            for (AmZpPlatforms platform : platforms) {
                if (Objects.equals(accouts.getPlatformId(), platform.getId())) {
                    accouts.setPlatform(platform.getName());
                    break;
                }
            }
        }

        resultVo.setList(list);
        resultVo.setCitys(citys);
        resultVo.setPlatforms(platforms);
        resultVo.setAccount_num(list.size());
        resultVo.setAccount_online_num(account_online_num);
        resultVo.setAccount_running_num(account_running_num);

        return ResultVO.success(resultVo);
    }

    public ResultVO<AmZpPlatformsResultVo> getPlatforms() {
        AmZpPlatformsResultVo resultVo = AmZpPlatformsResultVo.builder().platforms(amZpPlatformsService.list()).build();
        return ResultVO.success(resultVo);
    }

    public ResultVO addPlatform(String name) {
        int addPlatFormResult = amZpPlatformsService.addPlatForm(name);
        return addPlatFormResult > 0 ? ResultVO.success() : ResultVO.fail("平台添加失败！请联系管理员");
    }

    public ResultVO modifyPlatformName(Long id, String name) {
        boolean success = amZpPlatformsService.modifyPlatformName(id, name);
        return success ? ResultVO.success() : ResultVO.fail("名称修改失败！请联系管理员");
    }

    public ResultVO deletePlatformName(Long id) {
        boolean success = amZpPlatformsService.deletePlatformName(id);
        return success ? ResultVO.success() : ResultVO.fail("删除失败！请联系管理员");
    }


    public ResultVO addAccount(Long uid, Long platform_id, String account, String city) {

        QueryWrapper<AmZpLocalAccouts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account).eq("admin_id", uid).eq("type", "1");
        AmZpLocalAccouts accouts = amZpLocalAccoutsService.getOne(queryWrapper);

        if (accouts != null) {
            return ResultVO.fail("当前账号已存在");
        }

        AmZpPlatforms platforms = amZpPlatformsService.getById(platform_id);
        if (platforms == null) {
            return ResultVO.fail("平台数据异常");
        }

        AmAdmin amAdmin = amAdminService.getById(uid);//有两个账户表 user表没有对应属性，推测不允许发布招聘信息。
        if (amAdmin == null) {
            return ResultVO.fail("非招聘员用户，拒绝操作！");
        }

        boolean success = amZpLocalAccoutsService.addAmLocalAccount(uid, platform_id, platforms.getName(), account, amAdmin.getMobile(), city);
        return success ? ResultVO.success() : ResultVO.fail("添加成功！请联系管理员");
    }

    public ResultVO delAccount(String id) {
        AmZpLocalAccouts accouts = amZpLocalAccoutsService.getById(id);
        if (accouts == null) {
            return ResultVO.fail("账户不存在");
        }
        int delResult = amZpLocalAccoutsService.getBaseMapper().deleteById(id);
        return delResult > 0 ? ResultVO.success() : ResultVO.fail("删除失败，请稍后重试");
    }

    public ResultVO modifyRunningStatus(String id, int status) {

        AmZpLocalAccouts accouts = amZpLocalAccoutsService.getById(id);
        if (accouts == null) {
            return ResultVO.fail("账户不存在");
        }

        boolean success = amZpLocalAccoutsService.modifyRunningStatus(id, status);
        return success ? ResultVO.success() : ResultVO.fail("删除失败，请稍后重试");
    }


    public ResultVO getLoginQrcode(Long adminId){
        LambdaQueryWrapper<AmZpLocalAccouts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmZpLocalAccouts::getAdminId, adminId);
        AmZpLocalAccouts zpLocalAccouts = amZpLocalAccoutsService.getOne(queryWrapper, false);
        if (zpLocalAccouts == null) {
            return ResultVO.fail("账户不存在");
        }
        String extra = zpLocalAccouts.getExtra();
        if (StringUtils.isNotBlank(extra)) {
            JSONObject jsonObject = JSONObject.parseObject(extra);
            return ResultVO.success(jsonObject);
        }
        return ResultVO.fail("获取失败");
    }
}
