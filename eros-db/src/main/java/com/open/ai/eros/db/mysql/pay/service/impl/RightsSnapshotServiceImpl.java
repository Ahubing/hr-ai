package com.open.ai.eros.db.mysql.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.db.mysql.pay.entity.Rights;
import com.open.ai.eros.db.mysql.pay.entity.RightsSnapshot;
import com.open.ai.eros.db.mysql.pay.mapper.RightsSnapshotMapper;
import com.open.ai.eros.db.mysql.pay.service.IRightsSnapshotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */

@Slf4j
@Service
public class RightsSnapshotServiceImpl extends ServiceImpl<RightsSnapshotMapper, RightsSnapshot> implements IRightsSnapshotService {


    private final LoadingCache<Long, Optional<RightsSnapshot>> RIGHTS_SNAPSHOT_CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).
            initialCapacity(20).maximumSize(1000).build(new CacheLoader<Long, Optional<RightsSnapshot>>() {

        @Override
        public Optional<RightsSnapshot> load(Long aLong) throws Exception {
            Map<Long, Optional<RightsSnapshot>> longRightsSnapshotMap = loadRightsSnapshotMap(Lists.newArrayList(aLong));
            if (!longRightsSnapshotMap.get(aLong).isPresent()) {
                return Optional.empty();
            }
            return longRightsSnapshotMap.get(aLong);
        }

        @Override
        public Map<Long, Optional<RightsSnapshot>> loadAll(Iterable<? extends Long> ids) {
            //批量查询，调用getAll方法时，除去缓存中存在的，剩下的调用此方法批量查询，例如：[1,2,3,4]，缓存中存在2,3，那么调用此方法查询1,4
            return loadRightsSnapshotMap(Lists.newArrayList(ids));
        }

    });


    private Map<Long, Optional<RightsSnapshot>> loadRightsSnapshotMap(List<Long> ids) {
        List<RightsSnapshot> rightsSnapshots = this.listByIds(ids);
        return rightsSnapshots.stream().collect(Collectors.toMap(RightsSnapshot::getId, v -> Optional.of(v), (k1, k2) -> k1));
    }


    /**
     * 批量获取最新的快照信息
     * @param rightsIds
     * @return
     */
    public List<RightsSnapshot> batchGetLastRightsSnapshot(List<Long> rightsIds){

        List<RightsSnapshot> rightsSnapshots = new ArrayList<>();
        for (Long rightsId : rightsIds) {
            RightsSnapshot lastRightsSnapshot = getLastRightsSnapshot(rightsId);
            if(lastRightsSnapshot==null){
                continue;
            }
            rightsSnapshots.add(lastRightsSnapshot);
        }
        return rightsSnapshots;
    }



    /**
     * 获取当前权益最新的快照
     *
     * @param rightsId
     * @return
     */
    public RightsSnapshot getLastRightsSnapshot(Long rightsId) {
        return this.getBaseMapper().getLastRightsSnapshot(rightsId);
    }


    /**
     * 新增快照
     *
     * @param rights
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void addRightsSnapshot(Rights rights) {

        RightsSnapshot rightsSnapshot = new RightsSnapshot();

        rightsSnapshot.setCreateTime(LocalDateTime.now());
        rightsSnapshot.setIntro(rights.getIntro());
        rightsSnapshot.setEffectiveTime(rights.getEffectiveTime());
        rightsSnapshot.setName(rights.getName());
        rightsSnapshot.setRightsValue(rights.getRightsValue());
        rightsSnapshot.setRightsId(rights.getId());
        rightsSnapshot.setType(rights.getType());
        rightsSnapshot.setCanUseModel(rights.getCanUseModel());
        rightsSnapshot.setRule(rights.getRule());
        rightsSnapshot.setCanAdd(rights.getCanAdd());

        boolean saveResult = this.save(rightsSnapshot);
        log.info("addRightsSnapshot saveResult={},rightsSnapshot={}", saveResult, JSONObject.toJSONString(rightsSnapshot));
        if (!saveResult) {
            throw new BizException("新增快照失败！");
        }
    }


    /**
     * 获取单个权益内容
     *
     * @param id
     * @return
     */
    public RightsSnapshot getCacheRightsSnapshot(Long id) {
        try {
            Optional<RightsSnapshot> snapshotOptional = RIGHTS_SNAPSHOT_CACHE.get(id);
            return snapshotOptional.orElse(null);
        } catch (Exception e) {
            log.error("getCacheRightsSnapshot error id={}", id, e);
        }
        return null;
    }


    /**
     * 从缓存中获取权益快照
     *
     * @param ids
     * @return
     */
    public List<RightsSnapshot> getCacheRightsSnapshot(List<Long> ids) {
        List<RightsSnapshot> rightsSnapshots = new ArrayList<>();
        try {
            ImmutableMap<Long, Optional<RightsSnapshot>> immutableMap = RIGHTS_SNAPSHOT_CACHE.getAll(ids);
            for (Optional<RightsSnapshot> rightsSnapshot : immutableMap.values()) {
                rightsSnapshot.ifPresent(rightsSnapshots::add);
            }
        } catch (Exception e) {
            log.error("getCacheRightsSnapshot error ", e);
        }
        return rightsSnapshots;
    }


}

