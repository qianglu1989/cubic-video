package com.cubic.viedo.listener;

import com.cubic.viedo.event.FileDisposeEvent;
import com.cubic.viedo.store.DataStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName FileDisposeListener
 * @Author QIANGLU
 * @Date 2020/8/3 2:18 下午
 * @Version 1.0
 */
@Component
@Slf4j
public class FileDisposeListener {
    @Resource
    private DataStore dataStore;

    @EventListener(FileDisposeEvent.class)
    public void fileDispose(FileDisposeEvent fileDisposeEvent) {
        dataStore.disposeVideo(fileDisposeEvent.getChannel());
    }
}
