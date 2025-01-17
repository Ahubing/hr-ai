package com.open.ai.eros.creator.manager;

import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.creator.bean.RankTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Tuple;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author 
 * @Date 2024/1/31 20:05
 */
@Component
@Slf4j
public class RankManager {

    @Autowired
    private RedisClient jedisClient;

    public static String luaScript = "redis.call('ZINCRBY', KEYS[1], ARGV[1], ARGV[2]);" +
            "redis.call('ZINCRBY', KEYS[2], ARGV[1], ARGV[2]);" +
            "redis.call('ZINCRBY', KEYS[3], ARGV[1], ARGV[2]);" +
            "redis.call('ZINCRBY', KEYS[4], ARGV[1], ARGV[2])";


    /**
     * 根据传的时间type,转为成榜单的key
     * @param type
     * @return
     */
    public static String getMaskRankRedisKey(String type){
        Calendar calendar = Calendar.getInstance();
        String maskRankKey = "";
        switch(type) {
            case "hour":
                maskRankKey = "mask_rank_" + DateUtils.formatDate(new Date(),DateUtils.FORMAT_YYYY_MM_DDHH);
                break;
            case "day":
                maskRankKey = "mask_rank_" + DateUtils.formatDate(new Date(), DateUtils.FORMAT_YYYY_MM_DD);
                break;
            case "week":
                maskRankKey = "mask_rank_" +DateUtils.formatDate(new Date(),DateUtils.FORMAT_YYYY)+"_"+calendar.get(Calendar.WEEK_OF_YEAR);
                break;
            case "month":
                maskRankKey = "mask_rank_" +DateUtils.formatDate(new Date(),DateUtils.FORMAT_YYYY_MM);
                break;
            default:
                return DateUtils.formatDate(new Date(),DateUtils.FORMAT_YYYY_MM_DD_HH);
        }
        return maskRankKey;
    }



    public static Date getMaskRankStartTime(String type){
        Calendar calendar = Calendar.getInstance();
        switch(type) {
            case "hour":
                calendar.setTime(new Date());
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                return calendar.getTime();
            case "day":
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                return  calendar.getTime();
            case "week":
                calendar.setTime(new Date());
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                return calendar.getTime();
            case "month":
                calendar.setTime(new Date());
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                return calendar.getTime();
        }
        return new Date();
    }



    /**
     * 更新榜单
     * @param maskId
     */
    public void updateMaskRank(Long maskId){
        // lua 脚本同时更新小时、天、周、月榜单
       try {
           jedisClient.eval(luaScript, 4,
                   getMaskRankRedisKey(RankTypeEnum.hour.getKey()),
                   getMaskRankRedisKey(RankTypeEnum.day.getKey()),
                   getMaskRankRedisKey(RankTypeEnum.week.getKey()),
                   getMaskRankRedisKey(RankTypeEnum.month.getKey()),
                   "1", maskId.toString());
       }catch (Exception e){
           log.error("updateMaskRank error maskId={}",maskId,e);
       }

    }

    /**
     * 分页查询榜单
     * @param type
     * @param page
     * @param pageSize
     * @return
     */
    public Optional<List<Long>> getMaskIdsByRank(String type, Integer page,Integer pageSize){
       Integer beginIndex = Math.max((page - 1) * pageSize, 0);
       Integer endIndex =  (page) * pageSize <= 1 ? 0 : (page) * pageSize;
        Set<Tuple> tuples = jedisClient.zrevrangeWithScores(getMaskRankRedisKey(type), beginIndex, endIndex);
        List<Long> maskIds = tuples.stream()
                .map(tuple -> Long.valueOf(tuple.getElement()))
                .collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(maskIds) ? Optional.of(maskIds) : Optional.empty();
    }

}
