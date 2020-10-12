package com.cubic.viedo.constant;

/**
 * @Author QIANGLU
 * @Date 2020/3/20 1:37 下午
 * @Version 1.0
 */
public enum ChannelState {

    /**
     * mp4
     */
    OPEN(0),
    /**
     * webm
     */
    CLOSE(1);

    private Integer code;

    private ChannelState(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }


}
