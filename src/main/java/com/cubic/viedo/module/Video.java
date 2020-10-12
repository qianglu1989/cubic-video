package com.cubic.viedo.module;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName Video
 * @Author QIANGLU
 * @Date 2020/8/4 9:34 上午
 * @Version 1.0
 */
@Data
@Builder
public class Video {

    private String filePath;

    private Long duration;


}
