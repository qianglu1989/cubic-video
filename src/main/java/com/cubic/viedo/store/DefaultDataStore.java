package com.cubic.viedo.store;

import com.cubic.viedo.constant.ChannelState;
import com.cubic.viedo.constant.VideoType;
import com.cubic.viedo.mapper.VideoChannelsMapper;
import com.cubic.viedo.mapper.VideoInformationMapper;
import com.cubic.viedo.module.VideoDo;
import com.cubic.viedo.monitor.VideoChannelDo;
import com.cubic.viedo.utils.FileUploadUtils;
import com.cubic.viedo.utils.OSUtil;
import com.cubic.viedo.utils.ProcessUtil;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @ClassName DefaultWebConnectionStore
 * @Author QIANGLU
 * @Date 2020/4/6 11:13 上午
 * @Version 1.0
 */
@Slf4j
@Service
public class DefaultDataStore implements Runnable, DataStore {

    private final ConcurrentHashMap<Channel, String> datas = new ConcurrentHashMap<>();

//    private final ScheduledFuture future = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("DefaultDataStore")).scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);

    private final ExecutorService executorService = new ThreadPoolExecutor(OSUtil.getAvailableProcessors(), OSUtil.getAvailableProcessors() + 1, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(200), new DefaultThreadFactory("UploadVideo"));

    @Value("${matrix.video.dataPath:/data/videoData}")
    private String videoPath;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.secret:test}")
    private String appSecret;

    @Value("${file.appId:test}")
    private String fileAppId;

    @Resource
    private VideoInformationMapper videoInformationMapper;

    @Resource
    private VideoChannelsMapper videoChannelsMapper;

    @Override
    public void register(Channel channel, String data) {
        datas.put(channel, data);
        registerChannel(channel, data);
    }

    @Override
    public String getData(Channel channel) {
        return datas.get(channel);
    }

    @Override
    public Map<Channel, String> getAllDatas() {
        return datas;
    }

    @Override
    public void run() {
        try {
            for (Iterator<Map.Entry<Channel, String>> iter = datas.entrySet().iterator(); iter.hasNext(); ) {

                Map.Entry<Channel, String> entry = iter.next();
                Channel channel = entry.getKey();
                if (!channel.isActive()) {
                    executorService.execute(() -> {
                        disposeVideo(channel);
                    });
                }


            }
        } catch (Exception e) {
            log.error("DefaultDataStore Scheduled Error", e);
        }

    }


    @Override
    public void disposeVideo(Channel channel) {
        try {
            String serviceId = datas.remove(channel);
            StringBuilder builder = new StringBuilder();
            String name = builder.append(serviceId).append("_").append(channel.id().asShortText()).append(VideoType.WEBM.getCode()).toString();
            String orgPath = videoPath + channel.id().asLongText() + VideoType.WEBM.getCode();
            String defPath = videoPath + name;

            //存储文件基础数据
            VideoDo videoDo = VideoDo.builder().orgFileName(channel.id().asLongText()).serviceId(serviceId).data(serviceId).type(VideoType.MP4.getCode()).fileName(name).createDate(new Date()).modifyDate(new Date()).build();
            videoInformationMapper.insert(videoDo);

            File file = new File(orgPath);
            if (!file.exists()) {
                log.warn("文件：{} 不存在", orgPath);
                datas.remove(channel);
                return;
            }
//            Video video = VideoUtil.convertToMp4(defPath, orgPath);
//            boolean convert = convertFile(defPath, orgPath);

            //转换成功就上传转换过的，否则就上传原文件
            String result = FileUploadUtils.uploadFile(name, orgPath, fileAppId, uploadPath, appSecret);

            if (StringUtils.isNotEmpty(result)) {
                videoInformationMapper.updateVideoUrlByServiceId(result, serviceId);
                datas.remove(channel);
                FileUtils.deleteQuietly(new File(orgPath));
                updateChannelState(channel.id().asLongText(), ChannelState.CLOSE);
                log.info("上传完毕，删除文件内存数据Channel：{} ,保留原文件", name);
            } else {
                //上传失败时，重新搞
                datas.put(channel, serviceId);
            }


        } catch (Exception e) {
            log.error("异步转换传输文件失败", e);
        }

    }

    @Override
    public void remove(Channel channel) {
        datas.remove(channel);
    }

    /**
     * 转换文件类型
     *
     * @param defPath 目标类型
     * @param orgPath 源类型
     * @return
     */
    private boolean convertFile(String defPath, String orgPath) {
        //判断是否需要进行重新数据转换
        boolean convert = true;
        File defFile = new File(defPath);
        if (!defFile.exists()) {
            String cmd = ProcessUtil.instance().cmdStr(orgPath, defPath);
            boolean rs = ProcessUtil.instance().process(cmd);
            if (!rs) {
                defFile.delete();
                convert = false;
                log.info("文件转换mp4失败，准备上传原始文件：orgPath: {}", orgPath);
            }
        }
        return convert;
    }

    private void registerChannel(Channel channel, String data) {

        try {
            VideoChannelDo videoChannelDo = VideoChannelDo.builder().state(ChannelState.OPEN.getCode()).channelId(channel.id().asLongText()).fileName(channel.id().asLongText() + VideoType.WEBM.getCode()).remoteAddress(channel.remoteAddress().toString()).serverAddress(channel.localAddress().toString()).data(data).createDate(new Date()).modifyDate(new Date()).build();

            Long count = videoChannelsMapper.selectCountByChannelId(channel.id().asLongText());
            if (count == null || count == 0) {
                videoChannelsMapper.insert(videoChannelDo);
            }
        } catch (Exception e) {
            log.warn("insert registerChannel data error", e);
        }

    }

    private void updateChannelState(String channelId, ChannelState state) {

        try {
            videoChannelsMapper.updateState(channelId, state.getCode());
        } catch (Exception e) {
            log.warn("update  registerChannel state error", e);
        }

    }
}
