package com.cubic.viedo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * httpClient封装
 *
 * @author QIANG
 */
public class HttpClientUtils {

    private static Logger LOG = LoggerFactory.getLogger(HttpClientUtils.class);

    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int CONNECT_TIMEOUT = 100000;
    private static final int SOCKET_TIMEOUT = 100000;
    private static final int REQUEST_TIMEOUT = 2000;
    private static final String CHARSET = "UTF-8";

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(300);
        connMgr.setDefaultMaxPerRoute(300);

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 请求超时时间
        configBuilder.setConnectTimeout(CONNECT_TIMEOUT);
        // 连接不够时等待超时时间，不设置将阻塞线程
        configBuilder.setConnectionRequestTimeout(REQUEST_TIMEOUT);
        // 等待数据超时时间
        configBuilder.setSocketTimeout(SOCKET_TIMEOUT);
        LOG.debug("Http参数设置,连接超时时间[{}],Socket超时时间[{}],请求超时时间[{}]", CONNECT_TIMEOUT, SOCKET_TIMEOUT);
        requestConfig = configBuilder.build();
    }

    /**
     * 设置连接超时和请求超时
     *
     * @param connectTimeout
     * @param socketTimeout
     * @param connectionRequestTimeout
     */
    public static void setHttpParam(int connectTimeout, int socketTimeout, int connectionRequestTimeout) {

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(connectTimeout);
        configBuilder.setSocketTimeout(socketTimeout);
        configBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
        LOG.debug("Http参数自定义设置,请求超时时间[{}],等待数据超时时间[{}],连接不够时等待超时时间[{}]", connectTimeout, socketTimeout,
                connectionRequestTimeout);
        requestConfig = configBuilder.build();
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String doGet(String url) {
        try {
            String result = doGet(url, new HashMap<>(10));
            return result;
        } catch (Exception e) {
            LOG.error("发送 GET 请求ERROR :{}", e);
        }
        return "";
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String, Object> params) throws Exception {
        return doGet(url, params, CHARSET, new HashMap<>(16));
    }

    public static String doGet(String url, Map<String, Object> params, String charset, Map<String, String> headers) throws Exception {

        String result = null;

        if (StringUtils.isEmpty(url)) {
            LOG.debug("warn:doGet url is null or '' ");
            return result;
        }

        List<NameValuePair> pairList = new ArrayList<>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connMgr).build();
        CloseableHttpResponse response = null;
        InputStream instream = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setCharset(Charset.forName(StringUtils.isEmpty(charset) ? CHARSET : charset));
            uriBuilder.addParameters(pairList);
            URI uri = uriBuilder.build();
            HttpGet httpGet = new HttpGet(uri);

            headers.entrySet().forEach(entry -> {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            });

            response = httpclient.execute(httpGet);


            HttpEntity entity = response.getEntity();
            if (entity != null) {
                instream = entity.getContent();
                result = IOUtils.toString(instream, charset);
            }
        } finally {
            if (null != instream) {
                instream.close();
            }
            if (null != response) {
                response.close();
            }
            LOG.debug("close  instream response httpClient  connection succ");
        }
        return result;
    }

    public static HttpEntity doGetByDefault(String url) throws Exception {


        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connMgr).build();
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = httpclient.execute(httpGet);

        LOG.info("doGet statusCode:{}", response.getStatusLine().getStatusCode());

        return response.getEntity();


    }

    public static String doDelete(String url, Map<String, String> headers, Map<String, String> params) throws Exception {

        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connMgr).build();
        CloseableHttpResponse response = null;
        InputStream instream = null;
        List<NameValuePair> pairList = new ArrayList<>(params.size());
        params.entrySet().forEach(entry -> {
            pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

        });

        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setCharset(Charset.forName(CHARSET));
            uriBuilder.addParameters(pairList);
            URI uri = uriBuilder.build();
            HttpDelete httpDelete = new HttpDelete(uri);

            headers.entrySet().forEach(entry -> {
                httpDelete.setHeader(entry.getKey(), entry.getValue());
            });

            response = httpclient.execute(httpDelete);

            LOG.info("httpDelete statusCode:{}", response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                instream = entity.getContent();
                return IOUtils.toString(instream, CHARSET);
            }
        } finally {
            if (null != instream) {
                instream.close();
            }
            if (null != response) {
                response.close();
            }
            LOG.debug("close  instream response httpClient  connection succ");
        }
        return "";

    }

    /**
     * 发送 PUT 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String doPut(String url, Map<String, Object> params, String charset, Map<String, String> headers, String body) throws Exception {

        String result = null;

        if (StringUtils.isEmpty(url)) {
            return result;
        }

        List<NameValuePair> pairList = new ArrayList<>(params.size());
        params.entrySet().forEach(entry -> {
            pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));

        });

        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connMgr).build();
        CloseableHttpResponse response = null;
        InputStream instream = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setCharset(Charset.forName(StringUtils.isEmpty(charset) ? CHARSET : charset));
            uriBuilder.addParameters(pairList);
            URI uri = uriBuilder.build();
            HttpPut httpPut = new HttpPut(uri);

            if (!StringUtils.isEmpty(body)) {
                httpPut.setEntity(new StringEntity(body, "UTF-8"));
            }
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPut.setHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpclient.execute(httpPut);

            LOG.info("doGet statusCode:{}", response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                instream = entity.getContent();
                result = IOUtils.toString(instream, charset);
            }
        } finally {
            if (null != instream) {
                instream.close();
            }
            if (null != response) {
                response.close();
            }
            LOG.debug("close  instream response httpClient  connection succ");
        }
        return result;
    }


    /**
     * 发送 POST 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String doPost(String url) throws Exception {

        return doPost(url, new HashMap<>(16));
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param url    API接口URL
     * @param params 参数map
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> params) throws Exception {

        if (StringUtils.isEmpty(url)) {
            LOG.info("warn:doPost url is null or '' ");
            return null;
        }
        List<NameValuePair> pairList = new ArrayList<>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(CHARSET)));

        return send(httpPost, false);
    }

    /**
     * 发送 POST 请求（HTTP），xml
     *
     * @param url API接口URL
     * @param xml xml 参数
     * @return
     * @throws Exception
     */
    public static String doPost(String url, String xml) throws Exception {

        if (StringUtils.isEmpty(url)) {
            LOG.info("warn:doPost url is null or '' ");
            return null;
        }

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(xml, "GBK"));
        return send(httpPost, false);
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param url
     * @param json json对象
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Object json) throws Exception {

        if (StringUtils.isEmpty(url)) {
            LOG.error("warn:doPostByJson url is null or '' ");
            return null;
        }
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(json.toString(), CHARSET);
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);

        return send(httpPost, false);
    }

    public static String doPost(String url, HttpEntity entity, Map<String, String> headers) throws Exception {

        if (StringUtils.isEmpty(url)) {
            LOG.error("warn:doPostByJson url is null or '' ");
            return null;
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        headers.forEach((k, v) -> {
            httpPost.setHeader(k, v);

        });
        return send(httpPost, false);
    }

    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     * @throws Exception
     */
    public static String doPostSsl(String apiUrl, Map<String, Object> params) throws Exception {

        if (StringUtils.isEmpty(apiUrl)) {
            LOG.info("warn:doPostSSL url is null or '' ");
            return null;
        }

        List<NameValuePair> pairList = new ArrayList<>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
            pairList.add(pair);
        }
        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("utf-8")));
        return send(httpPost, true);
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式
     *
     * @param apiUrl API接口URL
     * @param json   JSON对象
     * @return
     * @throws Exception
     */
    public static String doPostSsl(String apiUrl, Object json) throws Exception {

        if (StringUtils.isEmpty(apiUrl)) {
            LOG.info("warn:doPostSSL By Json url is null or '' ");
            return null;
        }

        StringEntity stringEntity = new StringEntity(json.toString(), CHARSET);
        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setEntity(stringEntity);
        stringEntity.setContentEncoding(CHARSET);
        stringEntity.setContentType("application/json");
        return send(httpPost, true);
    }

    /**
     * 发送Post请求
     *
     * @param httpPost
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static String send(HttpPost httpPost, boolean isSsl) throws ClientProtocolException, IOException,
            KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        CloseableHttpClient httpClient = null;
        InputStream instream = null;
        String result = null;
        CloseableHttpResponse response = null;
        try {
            if (isSsl) {
                SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy())
                        .build();
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
                httpClient = HttpClients.custom().setConnectionManager(connMgr).setSSLSocketFactory(sslsf)
                        .setDefaultRequestConfig(requestConfig).build();
            } else {
                httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).setConnectionManager(connMgr)
                        .build();
            }
            response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                instream = entity.getContent();
                result = IOUtils.toString(instream, CHARSET);
            }
        } finally {
            if (null != instream) {
                instream.close();
            }
            if (null != response) {
                response.close();
            }
            LOG.debug("close  instream response httpClient  connection succ");
        }

        return result;
    }


    public static void checkResult(String result,String codeMsg){
        JSONObject data = JSON.parseObject(result);

        int resultCode = data.getInteger(StringUtils.isEmpty(codeMsg)?"errcode":codeMsg);

        if (resultCode != 0) {
            LOG.error("send http resultCode not 0,result:{}", result);
        }
    }

}
