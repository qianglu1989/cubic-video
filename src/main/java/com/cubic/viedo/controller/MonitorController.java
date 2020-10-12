package com.cubic.viedo.controller;

import com.alibaba.fastjson.JSON;
import com.cubic.viedo.module.ResponseBody;
import com.cubic.viedo.monitor.MonitorService;
import com.cubic.viedo.monitor.VideoChannelDo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/monitor")
@CrossOrigin
public class MonitorController {

    @Resource
    private MonitorService monitorService;

    @RequestMapping("/channels")
    public String getChannels() {
        ResponseBody<List<VideoChannelDo>> responseBody = new ResponseBody<>(0, monitorService.getChannels());
        return JSON.toJSONString(responseBody);
    }

}
