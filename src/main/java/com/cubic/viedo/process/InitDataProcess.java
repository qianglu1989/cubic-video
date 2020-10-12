package com.cubic.viedo.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cubic.viedo.webscoket.AbstractWebProcess;
import com.cubic.viedo.constant.CommandCode;
import com.cubic.viedo.store.DataStore;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName VideoProcess
 * @Author QIANGLU
 * @Date 2020/7/20 4:41 下午
 * @Version 1.0
 */
@Slf4j
@Service("initDataProcess")
public class InitDataProcess extends AbstractWebProcess {


    @Resource
    private DataStore dataStore;

    @Override
    public int code() {
        return CommandCode.INIT_DATA.getCode();
    }


    @Override
    public String process(Channel channel, String data) {
        JSONObject obj = JSON.parseObject(data);
        dataStore.register(channel, obj.getString("id"));
        log.info("Channel :{} register success,data{}", channel, data);
        return null;
    }
}
