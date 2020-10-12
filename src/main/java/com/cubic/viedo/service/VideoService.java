package com.cubic.viedo.service;

import com.cubic.viedo.module.VideoDo;

import java.util.List;

/**
 * @ClassName VideoService
 * @Author QIANGLU
 * @Date 2020/8/14 11:52 上午
 * @Version 1.0
 */
public interface VideoService {

    List<VideoDo> getVideoList(String id, Integer total);

    String getVideoUrl(String id);
}
