package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.open.ai.eros.common.util.HttpUtil;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmAdmin;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.entity.AmZpPlatforms;
import com.open.ai.eros.db.mysql.hr.service.impl.AmAdminServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmZpLocalAccoutsServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmZpPlatformsServiceImpl;
import com.open.hr.ai.bean.AmZpAccoutsResultVo;
import com.open.hr.ai.bean.AmZpPlatformsResultVo;
import com.open.hr.ai.constant.AmLocalAccountStatusEnums;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
            if (!AmLocalAccountStatusEnums.OFFLINE.getStatus().equals(accouts.getState())) {
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




    public ResultVO modifyStatus(String id, String status) {

        AmZpLocalAccouts accouts = amZpLocalAccoutsService.getById(id);
        if (accouts == null) {
            return ResultVO.fail("账户不存在");
        }
        LambdaUpdateWrapper<AmZpLocalAccouts> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AmZpLocalAccouts::getId, id).set(AmZpLocalAccouts::getState, status);
        if (AmLocalAccountStatusEnums.OFFLINE.getStatus().equals(accouts.getState())) {
            updateWrapper.set(AmZpLocalAccouts::getBrowserId,"");
        }

        boolean success = amZpLocalAccoutsService.update(updateWrapper);
        return success ? ResultVO.success() : ResultVO.fail("更新状态失败，请稍后重试");
    }


    public ResultVO getLoginQrcode(String bossId) {
        LambdaQueryWrapper<AmZpLocalAccouts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmZpLocalAccouts::getId, bossId);
        AmZpLocalAccouts zpLocalAccouts = amZpLocalAccoutsService.getOne(queryWrapper, false);
        if (zpLocalAccouts == null) {
            return ResultVO.fail("账户不存在");
        }
        if (AmLocalAccountStatusEnums.OFFLINE.getStatus().equals(zpLocalAccouts.getState())) {
            return ResultVO.success("账户已下线");
        }
        String extra = zpLocalAccouts.getExtra();
        if (StringUtils.isNotBlank(extra)) {
            JSONObject jsonObject = JSONObject.parseObject(extra);
            Object expires = jsonObject.get("expires");
            // 判断与当前时间戳,如果过期则返回为空
            if (System.currentTimeMillis() / 1000 > Long.parseLong(expires.toString())) {
                return ResultVO.success();
            }
            return ResultVO.success(jsonObject);
        }
        return ResultVO.success("二维码为空");
    }

    public ResultVO getJson(){
        try {


            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://gitee.com/yuehonghao/release/raw/master/jpy_client.json")
                    .get()
                    .build();
            Response execute = client.newCall(request).execute();
            String responseBody = execute.body().string();
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            return ResultVO.success(jsonObject);
        }catch (Exception e){
            log.error("getJson error",e);
        }
        return ResultVO.fail("获取失败");
    }
}
