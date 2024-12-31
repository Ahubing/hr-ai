package com.open.ai.eros.pay.goods.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.RightsRuleEnum;
import com.open.ai.eros.db.constants.RightsTypeEnum;
import com.open.ai.eros.db.mysql.pay.entity.Rights;
import com.open.ai.eros.db.mysql.pay.service.impl.RightsServiceImpl;
import com.open.ai.eros.db.mysql.pay.service.impl.RightsSnapshotServiceImpl;
import com.open.ai.eros.db.mysql.pay.service.impl.UserRightsServiceImpl;
import com.open.ai.eros.pay.goods.bean.req.RightsAddReq;
import com.open.ai.eros.pay.goods.bean.req.RightsUpdateReq;
import com.open.ai.eros.db.mysql.pay.entity.RightsSimpleVo;
import com.open.ai.eros.pay.goods.bean.vo.BRightsRuleVo;
import com.open.ai.eros.pay.goods.bean.vo.BRightsTypeVo;
import com.open.ai.eros.pay.goods.bean.vo.RightsVo;
import com.open.ai.eros.pay.goods.convert.RightsConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @类名：RightsManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/20 22:56
 */

@Component
public class RightsManager {


    @Autowired
    private RightsServiceImpl rightsService;


    @Autowired
    private RightsSnapshotServiceImpl rightsSnapshotService;


    @Autowired
    private UserRightsServiceImpl userRightsService;

    /**
     * 新增 权益
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO addRights(RightsAddReq req) {

        Rights rights = RightsConvert.I.convertRights(req);
        boolean save = rightsService.save(rights);
        if (!save) {
            throw new BizException("新增权益失败！");
        }
        rightsSnapshotService.addRightsSnapshot(rights);
        return ResultVO.success();
    }


    /**
     * 更新 权益
     *
     * @param req
     * @return
     */
    public ResultVO updateRights(RightsUpdateReq req) {
        Rights rights = RightsConvert.I.convertRights(req);
        boolean updateResult = rightsService.updateById(rights);
        if (!updateResult) {
            throw new BizException("修改权益失败！");
        }
        rightsSnapshotService.addRightsSnapshot(rights);
        return ResultVO.success();
    }


    /**
     * 分页获取权益
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResultVO<PageVO<RightsVo>> list(Integer status,Integer pageNum, Integer pageSize) {

        LambdaQueryWrapper<Rights> lambdaQueryChainWrapper = new LambdaQueryWrapper<>();

        lambdaQueryChainWrapper.eq(Rights::getStatus,status);

        lambdaQueryChainWrapper.orderByDesc(Rights::getCreateTime);

        Page<Rights> page = new Page<>(pageNum, pageSize);

        Page<Rights> rightsPage = rightsService.page(page, lambdaQueryChainWrapper);

        List<RightsVo> rightsVos = rightsPage.getRecords().stream().map(RightsConvert.I::convertRights).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(rightsPage.getTotal(), rightsVos));
    }



    public ResultVO<List<RightsSimpleVo>> getRightsSimpleVo(){
        List<RightsSimpleVo> rightsSimple = rightsService.getRightsSimple();
        return ResultVO.success(rightsSimple);
    }


    public ResultVO<List<BRightsTypeVo>> getRightsTypeVos(){
        List<BRightsTypeVo> rightsTypeVos = new ArrayList<>();
        for (RightsTypeEnum value : RightsTypeEnum.values()) {
            BRightsTypeVo bRightsTypeVo = new BRightsTypeVo();
            bRightsTypeVo.setType(value.getType());
            bRightsTypeVo.setTypeDesc(value.getDesc());
            rightsTypeVos.add(bRightsTypeVo);
        }
        return ResultVO.success(rightsTypeVos);
    }

    public ResultVO<List<BRightsRuleVo>> getRightsRuleVos(){
        List<BRightsRuleVo> rightsRuleVos = new ArrayList<>();
        for (RightsRuleEnum value : RightsRuleEnum.values()) {
            BRightsRuleVo bRightsRuleVo = new BRightsRuleVo();
            bRightsRuleVo.setRule(value.getRule());
            bRightsRuleVo.setRuleDesc(value.getDesc());
            rightsRuleVos.add(bRightsRuleVo);
        }
        return ResultVO.success(rightsRuleVos);
    }




}
