package com.cubic.viedo.utils;

import com.cubic.viedo.constant.VideoType;
import com.cubic.viedo.module.Video;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.*;
import sun.font.FontDesignMetrics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class VideoUtil {

    /**
     * 获取视频时长 单位/秒
     *
     * @param video
     * @return
     */
    public static long getVideoDuration(File video) {
        long duration = 0L;
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(video);
        try {
            ff.start();
            duration = ff.getLengthInTime() / (1000 * 1000);
            ff.stop();
        } catch (FrameGrabber.Exception e) {
            log.error("获取视频时长失败，fielName:{}", video.getName());
        }
        return duration;
    }

    /**
     * 转换视频文件为mp4
     *
     * @param defPath
     * @param orgPath
     * @return
     */
    public static Video convertToMp4(String defPath, String orgPath) {

        Video.VideoBuilder videoBuilder = Video.builder();

        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(orgPath);

        Frame captured_frame = null;

        FFmpegFrameRecorder recorder = null;


        try {
            frameGrabber.start();
            videoBuilder.duration(frameGrabber.getLengthInTime() / (1000 * 1000));

            recorder = new FFmpegFrameRecorder(defPath, frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels());
            //avcodec.AV_CODEC_ID_H264 AV_CODEC_ID_MPEG4
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat(VideoType.MP4.getCode());
            recorder.setFrameRate(frameGrabber.getFrameRate());
            //recorder.setSampleFormat(frameGrabber.getSampleFormat()); //
            recorder.setSampleRate(frameGrabber.getSampleRate());
            recorder.setAudioChannels(frameGrabber.getAudioChannels());
            recorder.setFrameRate(frameGrabber.getFrameRate());
            recorder.start();

            //添加水印

            while ((captured_frame = frameGrabber.grabFrame()) != null) {
                try {
                    Java2DFrameConverter converter = new Java2DFrameConverter();
                    BufferedImage bufferedImage = converter.getBufferedImage(captured_frame);
                    Frame frame = converter.getFrame(mark(bufferedImage));
                    recorder.setTimestamp(frameGrabber.getTimestamp());
                    recorder.record(frame);

                } catch (Exception e) {
                }
            }
            recorder.stop();
            recorder.release();
            frameGrabber.stop();
            videoBuilder.filePath(defPath);
        } catch (Exception e) {
            log.error("File :{} Convert MP4 Error ", orgPath, e);
            return null;
        }
        return videoBuilder.build();
    }

    public static BufferedImage mark(BufferedImage bufImg) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Font font = new Font("微软雅黑", Font.BOLD, 64);
        String content = DateUtils.getDateFormat(new Date());
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);

        Graphics2D graphics = bufImg.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        //设置图片背景
        graphics.drawImage(bufImg, 0, 0, bufImg.getWidth(), bufImg.getHeight(), null);
        //设置左上方时间显示
        graphics.setColor(Color.green);
        graphics.setFont(new Font("微软雅黑", Font.BOLD, 16));
        graphics.drawString(content, 0, metrics.getAscent());
        graphics.dispose();
        return bufImg;
    }
}
