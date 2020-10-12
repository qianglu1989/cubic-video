package com.cubic.viedo.constant;

/**
 * @ClassName ResponseCode
 * @Author QIANGLU
 * @Date 2020/3/20 1:37 下午
 * @Version 1.0
 */
public enum VideoType {

    /**
     * mp4
     */
    MP4(".mp4"),
    /**
     * webm
     */
    WEBM(".webm");

    private String code;

    private VideoType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }


}
