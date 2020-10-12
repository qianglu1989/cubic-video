package com.cubic.viedo.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cubic.viedo.module.ResponseBody;
import com.cubic.viedo.service.VideoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName VideoController
 * @Author QIANGLU
 * @Date 2020/8/4 5:45 下午
 * @Version 1.0
 */
@RestController
@CrossOrigin
public class VideoController {

    @Resource
    private VideoService videoService;

    @RequestMapping("/getVideo")
    public String getVideo(@RequestParam String id) {
        return videoService.getVideoUrl(id);
    }

    @RequestMapping("/getVideoList")
    public String getVideoList(@RequestParam(required = false, defaultValue = "") String id, @RequestParam(required = false, defaultValue = "50") Integer total) {
        ResponseBody response = new ResponseBody(0, videoService.getVideoList(id, total));
        return JSONObject.toJSONString(response, SerializerFeature.WriteDateUseDateFormat);
    }
}
