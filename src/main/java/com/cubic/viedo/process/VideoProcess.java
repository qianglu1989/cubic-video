package com.cubic.viedo.process;

import com.cubic.viedo.webscoket.AbstractWebProcess;
import com.cubic.viedo.constant.CommandCode;
import com.cubic.viedo.constant.VideoType;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @ClassName VideoProcess
 * @Author QIANGLU
 * @Date 2020/7/20 4:41 下午
 * @Version 1.0
 */
@Slf4j
@Service("videoProcess")
public class VideoProcess extends AbstractWebProcess {



    @Value("${matrix.video.dataPath:/data/videoData}")
    private String videoPath;

    @Override
    public int code() {
        return CommandCode.VIDEO.getCode();
    }

    @Override
    public String process(String id,ByteBuf byteBuf) {

        try {
            File file = new File(videoPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(new File(videoPath + id + VideoType.WEBM.getCode()), "rw");
            randomAccessFile.seek(randomAccessFile.length());
            if (byteBuf.isReadable()) {
                byteBuf.readBytes(randomAccessFile.getChannel(), byteBuf.readableBytes());
            }
        } catch (Exception e) {
            log.error("");
        }

        return null;
    }
}
