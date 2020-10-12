package com.cubic.viedo.monitor;

import com.cubic.viedo.mapper.VideoChannelsMapper;
import com.cubic.viedo.store.DataStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName MonitorServiceImpl
 * @Author QIANGLU
 * @Date 2020/8/14 2:29 下午
 * @Version 1.0
 */
@Service
public class MonitorServiceImpl implements MonitorService {
    @Resource
    private DataStore dataStore;

    @Value("${matrix.video.dataPath:/data/videoData}")
    private String videoPath;

    @Resource
    private VideoChannelsMapper videoChannelsMapper;
    @Override
    public List<VideoChannelDo> getChannels() {
//        List<ChannelVo> datas = new ArrayList<>();
//        dataStore.getAllDatas().entrySet().forEach(entry -> {
//            Channel channel = entry.getKey();
//            String id = channel.id().asLongText();
//            ChannelVo vo = ChannelVo.builder().state(channel.isActive()?"正在传输":"待上传文件").id(id).fileName(id + VideoType.WEBM.getCode()).filePath(videoPath).remoteAddress(channel.remoteAddress().toString()).serverAddress(channel.localAddress().toString()).data(entry.getValue()).build();
//            datas.add(vo);
//        });
        return videoChannelsMapper.selectChannels();
    }
}
