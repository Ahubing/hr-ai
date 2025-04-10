package com.open.ai.eros.pay.goods.controller;


import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.BalanceUnitEnum;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.pay.config.PayBaseController;
import com.open.ai.eros.pay.goods.bean.req.GoodsAddReq;
import com.open.ai.eros.pay.goods.bean.req.GoodsSearchReq;
import com.open.ai.eros.pay.goods.bean.req.GoodsUpdateReq;
import com.open.ai.eros.pay.goods.bean.vo.BalanceUnitVo;
import com.open.ai.eros.pay.goods.bean.vo.GoodsSearchResult;
import com.open.ai.eros.pay.goods.bean.vo.GoodsVo;
import com.open.ai.eros.pay.goods.manager.GoodsManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */

@Api(tags = "商品控制类")
@RestController
public class GoodsController extends PayBaseController {


    @Autowired
    private GoodsManager goodsManager;


    @VerifyUserToken(role = RoleEnum.SYSTEM)
    @ApiOperation("商品新增")
    @PostMapping("/goods/add")
    public ResultVO addGoods(@Valid @RequestBody GoodsAddReq req){

        return goodsManager.addGoods(getUserId(),req);
    }


    @VerifyUserToken(role = RoleEnum.SYSTEM)
    @ApiOperation("商品更新")
    @PostMapping("/goods/update")
    public ResultVO updateGoods(@Valid @RequestBody GoodsUpdateReq req){

        return goodsManager.updateGoods(getUserId(),req);
    }


    @VerifyUserToken
    @ApiOperation("c-商品列表")
    @GetMapping("/goods/c/list")
    public ResultVO<GoodsSearchResult> cSearch(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                               @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                               @RequestParam(value = "type",required = true) String type){
        return goodsManager.cSearch(pageNum,pageSize,type);
    }



    @VerifyUserToken(role = RoleEnum.SYSTEM)
    @ApiOperation("下架商品")
    @PostMapping("/goods/delete")
    public ResultVO<GoodsSearchResult> delete(@RequestParam(value = "id",required = true) Long id){
        return goodsManager.deleteGoods(id);
    }



    @VerifyUserToken(role = RoleEnum.SYSTEM)
    @ApiOperation("b-商品列表")
    @PostMapping("/goods/b/list")
    public ResultVO<PageVO<GoodsVo>> bSearch(@RequestBody @Valid GoodsSearchReq req){
        return goodsManager.searchGoods(req);
    }


    @VerifyUserToken(role = RoleEnum.SYSTEM)
    @ApiOperation("b-金钱单位列表")
    @GetMapping("/goods/b/unit")
    public ResultVO<List<BalanceUnitVo>> getBalanceUnit(){
        List<BalanceUnitVo> maskTypeVos = Arrays.stream(BalanceUnitEnum.values()).map(e -> {
            BalanceUnitVo maskTypeVo = new BalanceUnitVo();
            maskTypeVo.setUnit(e.getUnit());
            maskTypeVo.setDesc(e.getDesc());
            return maskTypeVo;
        }).collect(Collectors.toList());
        return ResultVO.success(maskTypeVos);
    }


}

