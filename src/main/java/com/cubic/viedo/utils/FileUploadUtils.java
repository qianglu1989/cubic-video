package com.cubic.viedo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.HashMap;

/**
 * @ClassName FileSystemUtils
 * @Author QIANGLU
 * @Date 2019/12/4 2:08 下午
 * @Version 1.0
 */
public class FileUploadUtils {

    private static String DEV_PATH = "http://172.17.105.26:6080/file-server/api/v1/upload/single";

    private static Logger logger = LoggerFactory.getLogger(FileUploadUtils.class);

    private static String APP_SECRET = "cubic";

    /**
     * 上传文件
     *
     * @param fileName 文件名称
     * @param path     内容
     * @param appid    应用唯一标识
     * @param uri      发送地址
     */
    public static String uploadFile(String fileName, String path, String appid, String uri, String appSecret) {

        try {
            if (StringUtils.isEmpty(uri)) {
                logger.warn("uploadFile uri can not null");
                return "";
            }
            logger.info("开始进行文件上传，fileName：{} , path: {} ,appid:{} ",fileName,path,appid);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("fileName", fileName);
            builder.addTextBody("appId", appid);
            builder.addTextBody("appSecret", appSecret);
            builder.addTextBody("storageClass", "2");
            builder.addTextBody("isDownloadLink", "1");
            builder.addBinaryBody("file", new FileInputStream(path), ContentType.MULTIPART_FORM_DATA, fileName);
            HttpEntity entity = builder.build();
            String result = HttpClientUtils.doPost(uri, entity, new HashMap<>());
            JSONObject obj = JSON.parseObject(result);
            Integer code = obj.getInteger("code");

            if (code == null && code.intValue() != 0) {
                logger.error("upload fail :{}", result);
                return "";
            }
            logger.info("upload succ -> filename:{},result:{}", fileName, result);
            JSONObject data = obj.getJSONObject("data");
            return data.getString("filePath");
        } catch (Exception e) {
            logger.warn("upload file fial,fileName:{},appid:{}.,error:{}", fileName, appid, e);
        }
        return "";
    }


    public static String downloadFile(String fileName, String appid, String uri) {

        if (StringUtils.isEmpty(uri)) {
            logger.warn("uploadFile uri can not null");
            return null;
        }

        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("appId", appid);
            builder.addTextBody("fileName", fileName);
            builder.addTextBody("policy", "1");
            builder.addTextBody("appSecret", APP_SECRET);

            HttpEntity entity = builder.build();
            String result = HttpClientUtils.doPost(uri, entity, new HashMap<>());
            JSONObject datas = JSON.parseObject(result);
            JSONObject data = datas.getJSONObject("data");
            return data != null ? data.getString("filePath") : null;
        } catch (Exception e) {
            logger.error("下载文件出错,fileName:{},appid:{},error:{}", fileName, appid, e);
        }
        return null;
    }
}
