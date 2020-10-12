package com.cubic.viedo.monitor;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @ClassName Video
 * @Author QIANGLU
 * @Date 2020/8/4 9:34 上午
 * @Version 1.0
 */
@Data
@Builder
public class VideoChannelDo {

    private Integer id;

    private String channelId;

    private String remoteAddress;

    private String serverAddress;


    /**
     * 文件名称
     */
    private String fileName;


    private Integer state;


    private String data;

    private Date createDate;

    private Date modifyDate;

    @Tolerate
    VideoChannelDo() {
    }


}
