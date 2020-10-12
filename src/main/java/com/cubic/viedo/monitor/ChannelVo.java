package com.cubic.viedo.monitor;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @ClassName ChannelDo
 * @Author QIANGLU
 * @Date 2020/8/14 2:25 下午
 * @Version 1.0
 */
@Data
@Builder
public class ChannelVo {

    private String id;

    private String remoteAddress;

    private String serverAddress;

    private String fileName;

    private String filePath;

    private String state;

    private String data;
    @Tolerate
    ChannelVo() {
    }
}
