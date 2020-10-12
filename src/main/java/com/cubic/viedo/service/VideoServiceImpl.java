package com.cubic.viedo.service;

import com.cubic.viedo.mapper.VideoInformationMapper;
import com.cubic.viedo.module.VideoDo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName VideoServiceImpl
 * @Author QIANGLU
 * @Date 2020/8/14 11:52 上午
 * @Version 1.0
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Resource
    private VideoInformationMapper videoInformationMapper;

    @Override
    public List<VideoDo> getVideoList(String id, Integer total) {
        return videoInformationMapper.selectVideos(total, StringUtils.isEmpty(id) ? null : id);
    }


    @Override
    public String getVideoUrl(String id) {
        return videoInformationMapper.selectByServiceId(id);
    }
}
