package com.cjh.watching.watchback.utils;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @Description: http请求工具类
 * @Author: xuhaibin
 * @Date: 2023/3/24 14:48
 */
public class HttpClientUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    /**
     * 请求连接构造对象
     */
    private static final HttpClientBuilder httpClientBuilder = HttpClients.custom();

    /**
     * 连接池最大连接数
     */
    private static final int MAX_TOTAL = 8;

    /**
     * 每个路由最大默认连接数
     */
    private static final int DEFAULT_MAX_RER_ROUTE = 8;

    /**
     * 获取连接获取超时时间
     */
    private static final int CONNECTION_REQUEST_TIMEOUT = 2000;

    /**
     * 连接超时时间
     */
    private static final int CONNECTION_TIMEOUT = 10000;

    /**
     * 数据响应超时时间
     */
    private static final int SOCKET_TIMEOUT = 30000;

    static {
        /*
         1、绕开不安全的https请求的证书验证(不需要可以注释，然后使用空参数的PoolingHttpClientConnectionManager构造连接池管理对象)
         */
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build();

        /*
         2、创建请求连接池管理
         */
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 设置连接池最大连接数
        cm.setMaxTotal(MAX_TOTAL);
        // 设置每个路由最大默认连接数
        cm.setDefaultMaxPerRoute(DEFAULT_MAX_RER_ROUTE);
        httpClientBuilder.setConnectionManager(cm);

        /*
        3、设置默认请求配置
         */
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT) // 设置获取连接获取超时时间
                .setConnectTimeout(CONNECTION_TIMEOUT) // 设置连接超时时间
                .setSocketTimeout(SOCKET_TIMEOUT) // 设置数据响应超时时间
                .build();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
    }

    /**
     * @Description: 执行post请求
     * @Author: xuhaibin
     * @Date: 2023/3/24 14:50
     * @Param: [url, headers]
     * @Return: java.lang.String
     */
    public static String getJson(String url, Map<String, String> headers) {
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpGet httpGet = new HttpGet(url);
        // 请求头设置，如果常用的请求头设置，也可以写死，特殊的请求才传入
        if (headers != null) {
            for (String headerKey : headers.keySet()) {
                httpGet.setHeader(headerKey, headers.get(headerKey));
            }
        }
        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode== HttpStatus.SC_OK) { // 请求响应成功
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            } else {
                logger.error("请求地址({})失败:{}", url, statusCode);
            }
        } catch (IOException e) {
            logger.error("请求地址({})失败", url, e);
            throw new RuntimeException("请求地址("+url+")失败");
        } finally {
            org.apache.http.client.utils.HttpClientUtils.closeQuietly(response);
        }
        return null;
    }

    /**
     * @Description: 执行post请求
     * @Author: xuhaibin
     * @Date: 2023/3/24 14:49
     * @Param: [url, headers, params]
     * @Return: java.lang.String
     */
    public static String postJson(String url, Map<String, String> headers, Object params) {
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(url);
        // 请求头设置，如果常用的请求头设置，也可以写死，特殊的请求才传入
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        if (headers != null) {
            for (String headerKey : headers.keySet()) {
                httpPost.setHeader(headerKey, headers.get(headerKey));
            }
        }
        if (params!=null) {
            HttpEntity paramEntity = new StringEntity(JSON.toJSONString(params), StandardCharsets.UTF_8);
            httpPost.setEntity(paramEntity);
        }

        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode== HttpStatus.SC_OK) { // 请求响应成功
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            } else {
                logger.error("请求地址({})失败:{}", url, statusCode);
            }
        } catch (IOException e) {
            logger.error("请求地址({})失败", url, e);
            throw new RuntimeException("请求地址("+url+")失败");
        } finally {
            org.apache.http.client.utils.HttpClientUtils.closeQuietly(response);
        }
        return null;
    }

    public static String postFile(String url, Map<String, String> headers, List<File> files,String fileParamName) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(600000).setConnectTimeout(600000).setConnectionRequestTimeout(600000).build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        // 请求头设置，如果常用的请求头设置，也可以写死，特殊的请求才传入
        if (headers != null) {
            for (String headerKey : headers.keySet()) {
                httpPost.setHeader(headerKey, headers.get(headerKey));
            }
        }
        if(files != null && files.size() > 0){
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.setContentType(ContentType.MULTIPART_FORM_DATA);
            for(File file : files){
                FileBody fileBody = new FileBody(file, ContentType.MULTIPART_FORM_DATA, file.getName());
                builder.addPart(fileParamName, fileBody);
            }
            HttpEntity httpEntity = builder.build();
            httpPost.setEntity(httpEntity);
        }

        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode== HttpStatus.SC_OK) { // 请求响应成功
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            } else {
                logger.error("请求地址({})失败:{}", url, statusCode);
            }
        } catch (IOException e) {
            logger.error("请求地址({})失败", url, e);
            throw new RuntimeException("请求地址("+url+")失败");
        } finally {
            org.apache.http.client.utils.HttpClientUtils.closeQuietly(response);
        }
        return null;
    }
}