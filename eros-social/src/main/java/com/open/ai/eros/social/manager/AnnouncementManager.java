package com.open.ai.eros.social.manager;

import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.social.entity.Announcement;
import com.open.ai.eros.db.mysql.social.entity.AnnouncementSearchVo;
import com.open.ai.eros.db.mysql.social.mapper.AnnouncementMapper;
import com.open.ai.eros.social.bean.req.AddAnnouncementReq;
import com.open.ai.eros.social.bean.req.SearchAnnouncementReq;
import com.open.ai.eros.social.bean.req.UpdateAnnouncementReq;
import com.open.ai.eros.social.bean.vo.AnnouncementVo;
import com.open.ai.eros.social.convert.AnnouncementAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AnnouncementManager {

    @Resource
    private AnnouncementMapper announcementConfigMapper;


    @Resource
    private AnnouncementAdapter adapter;


    // 获取最新的
    public ResultVO<AnnouncementVo> getBestAnnouncementVo(){
        Announcement bestAnnouncement = announcementConfigMapper.getBestAnnouncement();
        if(bestAnnouncement==null){
            return ResultVO.success();
        }
        return ResultVO.success(adapter.convert(bestAnnouncement));
    }



    /**
     * 新增公告
     *
     * @param addAnnouncementReq
     * @param account
     * @return
     */
    public int addAnnouncementConfig(AddAnnouncementReq addAnnouncementReq, String account) {
        // 将请求参数转换为 Announcement 对象
        Announcement announcement = new Announcement();
        announcement.setCreateUser(account);
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        announcement.setTitle(addAnnouncementReq.getTitle());
        announcement.setContent(addAnnouncementReq.getContent());
        announcement.setStatus(addAnnouncementReq.getStatus());
        announcement.setType(addAnnouncementReq.getType());
        announcement.setDuration(addAnnouncementReq.getDuration());
        // 调用方法进行新增
        return announcementConfigMapper.insert(announcement);
    }

    /**
     * 查询公告
     *
     * @return
     */
    public List<AnnouncementVo> getAllAnnouncementConfig() {

        List<Announcement> announcementConfigs = announcementConfigMapper.selectListAnnouncementConfig();
        // 判断是否查询到数据
        if (CollectionUtils.isNotEmpty(announcementConfigs)) {
            // 将 Announcement 对象转换为 AnnouncementVO 对象
            return announcementConfigs.stream().map(adapter::convert).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 删除公告
     *
     * @param id
     * @return
     */
    public ResultVO deleteAnnouncement(Long id) {
        Announcement announcementConfig = announcementConfigMapper.selectById(id);
        if (announcementConfig == null) {
            return ResultVO.fail("公告不存在");
        }
        // 调用方法进行删除
        int result = announcementConfigMapper.deleteById(id);
        if (result <= 0) {
            return ResultVO.fail("删除失败，系统繁忙");
        }
        return ResultVO.success();
    }

    /**
     * 更新公告
     *
     * @param req
     * @return
     */
    public int updateAnnouncement(UpdateAnnouncementReq req, String account) {
        // 将请求参数转换为 Announcement 对象
        Announcement announcement = new Announcement();
        announcement.setUpdateUser(account);
        announcement.setId(req.getId());
        announcement.setContent(req.getContent());
        announcement.setTitle(req.getTitle());
        announcement.setStatus(req.getStatus());
        announcement.setType(req.getType());
        announcement.setUpdateTime(LocalDateTime.now());
        announcement.setDuration(req.getDuration());
        // 调用方法进行更新
        return announcementConfigMapper.updateById(announcement);
    }


    /**
     * 查询公告
     *
     * @return
     */
    public PageVO<AnnouncementVo> getAnnouncementConfig(SearchAnnouncementReq req) {
        AnnouncementSearchVo build = AnnouncementSearchVo.builder()
                .title(req.getTitle())
                .status(req.getStatus())
                .type(req.getType())
                .id(req.getId())
                .pageIndex((req.getPageNum() - 1) * req.getPageSize())
                .pageSize(req.getPageSize())
                .build();

        PageVO<AnnouncementVo> pageVO = new PageVO<>();
        int count = announcementConfigMapper.searchAnnouncementConfigCount(build);
        pageVO.setTotal(count);
        if (count > 0) {
            List<Announcement> announcementConfigs = announcementConfigMapper.searchAnnouncementConfig(build);
            pageVO.setData(announcementConfigs.stream().map(adapter::convert).collect(Collectors.toList()));
        }
        return pageVO;
    }
}
