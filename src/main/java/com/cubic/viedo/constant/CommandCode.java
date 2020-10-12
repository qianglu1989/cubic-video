package com.cubic.viedo.constant;

/**
 * @ClassName ResponseCode
 * @Author QIANGLU
 * @Date 2020/3/20 1:37 下午
 * @Version 1.0
 */
public enum CommandCode {

    /**
     * 心跳命令
     */
    HEART(0),
    /**
     * 视频流
     */
    VIDEO(1),

    /**
     * 业务功能参数
     */
    INIT_DATA(2);

    private Integer code;

    private CommandCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }


}
