package com.open.ai.eros.pay.util;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.util.ObjectToHashMapConverter;
import com.open.ai.eros.db.privacy.utils.CryptoUtil;
import com.open.ai.eros.pay.config.PayConfig;
import com.open.ai.eros.pay.order.bean.dto.GetOutOrderDto;
import com.open.ai.eros.pay.order.bean.dto.GetOutOrderResponseDto;
import com.open.ai.eros.pay.order.bean.dto.GetPayUrlDto;
import com.open.ai.eros.pay.order.bean.dto.GetPayUrlResponseDto;
import com.open.ai.eros.pay.vo.CurrencyRateVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @类名：PayHttpUtil
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/27 20:28
 */

@Component
@Slf4j
public class PayHttpUtil {

    @Autowired
    private PayConfig payConfig;

    public OkHttpClient client;


    public PayHttpUtil() {
        client = new OkHttpClient.Builder()
                .protocols( Collections.singletonList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(1000, 300, TimeUnit.SECONDS))
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }




    public static CurrencyRateVo balanceRate() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols( Collections.singletonList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(1000, 300, TimeUnit.SECONDS))
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        String today = DateUtils.formatDate(new Date(),DateUtils.FORMAT_YYYY_MM_DD);

        String url = "https://www.chinamoney.com.cn/ags/ms/cm-u-bk-ccpr/CcprHisNew";
        Map<String, String> query = new HashMap<>(4);
        query.put("startDate", today);
        query.put("endDate", today);
        query.put("pageNum", "1");
        query.put("head","USD/CNY");
        query.put("pageSize", "30");
        String linkString = ChPayUtil.createLinkString(query);
        Request.Builder builder = new Request.Builder().url(url+"?"+linkString).get();
        Request request = builder.build();
        Response execute = client.newCall(request).execute();
        ResponseBody responseBody = execute.body();
        assert responseBody != null;
        String payResult = responseBody.string();
        return JSONObject.parseObject(payResult, CurrencyRateVo.class);
    }

    public static void main(String[] args) throws IOException {
        balanceRate();
    }


    /**
     * act=order&pid={商户ID}&key={商户密钥}&out_trade_no={商户订单号}
     * @param dto
     * @return
     */
    public GetOutOrderResponseDto queryOrderStatus(GetOutOrderDto dto){
        String uri = String.format("?act=order&pid=%s&key=%s&trade_no=%s",payConfig.getPid(),payConfig.getSign(),dto.getTrade_no());
        String url = payConfig.getCdn() + payConfig.getOrderQueryUrl()+uri;

        try {
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();
            Response execute = client.newCall(request).execute();
            ResponseBody responseBody = execute.body();
            assert responseBody != null;
            String payResult = responseBody.string();
            return JSONObject.parseObject(payResult, GetOutOrderResponseDto.class);
        }catch (Exception e){
            log.error("queryOrderStatus dto={} ",JSONObject.toJSONString(dto),e);
        }
        return null;
    }



    /**
     * 获取支付的链接
     *
     * @return
     */
    public GetPayUrlResponseDto getOrderPayUrl(GetPayUrlDto req){
        Map<String, String> stringStringMap = ObjectToHashMapConverter.convertObjectToHashMap(req);
        Map<String, String> sortMap = ChPayUtil.argSort(stringStringMap);
        /**
         * 请求的参数
         */
        String reqStr = ChPayUtil.createLinkString(sortMap);
        String signType = payConfig.getSignType();
        String sign = payConfig.getSign();

        try {
            String encryptMd5 = CryptoUtil.encryptMd5((reqStr + sign));
            // 将配置中的签名和排序好的参数放进去
            req.setSign_type(signType);
            req.setSign(encryptMd5);
            stringStringMap = ObjectToHashMapConverter.convertObjectToHashMap(req);
            reqStr = ChPayUtil.createLinkString(stringStringMap);
            String payUrl = payConfig.getCdn() + payConfig.getPayUrl()+"?"+reqStr;
            Request.Builder builder = new Request.Builder().url(payUrl).get();

            Request request = builder.build();
            Response execute = this.client.newCall(request).execute();
            ResponseBody responseBody = execute.body();
            assert responseBody != null;
            String payResult = responseBody.string();
            log.info("getOrderPayUrl reqStr={} payResult={}",reqStr,payResult);
            return JSONObject.parseObject(payResult, GetPayUrlResponseDto.class);
        }catch (Exception e){
            log.error("getOrderPayUrl error reqStr={}",reqStr,e);
            throw new BizException(e.getMessage());
        }
    }



}
