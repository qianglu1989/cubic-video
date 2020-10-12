package com.cubic.viedo.module;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @ClassName VideoPo
 * @Author QIANGLU
 * @Date 2020/7/27 11:24 上午
 * @Version 1.0
 */
@Data
@Builder
public class VideoDo {

    private Integer id;

    /**
     * 业务唯一标识
     */
    private String serviceId;

    /**
     * 原始文件名称
     */
    private String orgFileName;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String type;

    private String fileUrl;

    private String data;

    private Date createDate;

    private Date modifyDate;

    @Tolerate
    VideoDo() {
    }
}
