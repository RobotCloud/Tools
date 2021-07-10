package com.robot.utils;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 基于okhttp的HTTP工具类</p>
 */
public class HttpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
    private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
    private static final String TEXT_PLAIN_UTF8 = "text/plain; charset=utf-8";
    private final OkHttpClient okHttpClient;
    private volatile static HttpUtil inst;

    private HttpUtil() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            builder.connectTimeout(60L, TimeUnit.SECONDS);
            builder.readTimeout(60L, TimeUnit.SECONDS);
            builder.writeTimeout(60L, TimeUnit.SECONDS);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        this.okHttpClient = builder.build();
    }

    public static HttpUtil getInst() {
        if (inst == null) {
            synchronized (HttpUtil.class) {
                if (inst == null) {
                    inst = new HttpUtil();
                }
            }
        }
        return inst;
    }

    /**
     * <p>Title: 发送GET请求</p>
     * <p>Create Time: 2020/9/2 17:09</p>
     */
    public String doGet(String url) {
        return doGet(url, null, null);
    }

    /**
     * <p>Title: 发送GET请求</p>
     * <p>Create Time: 2020/9/2 17:09</p>
     */
    public String doGet(String url, Map<String, String> params) {
        return doGet(url, params, null);
    }

    /**
     * <p>Title: 发送GET请求</p>
     * <p>Create Time: 2020/9/2 17:09</p>
     */
    public String doGet(String url, Map<String, String> params, Map<String, String> headers) {
        LOGGER.info("请求类型: <GET>, 请求URL: <{}>", url);
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url), "URL错误！请检查是否缺少协议、域名（或IP）、端口号！").newBuilder();
        Request.Builder requestBuilder = new Request.Builder();
        // 请求头设置
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            LOGGER.info("请求头（值为null的会被过滤）: <{}>", headers.toString());
            this.removeEmptyValue(headers);
            requestBuilder.headers(Headers.of(headers));
        }
        // 请求参数设置
        if (Objects.nonNull(params) && !params.isEmpty()) {
            LOGGER.info("请求参数（值为null的会被过滤）: <{}>", params.toString());
            this.removeEmptyValue(params);
            params.forEach(urlBuilder :: addEncodedQueryParameter);
        }
        return this.send(requestBuilder.url(urlBuilder.build()).get().build());
    }

    /**
     * <p>Title: 发送POST请求</p>
     * <p>Description: 请求参数以application/x-www-form-urlencoded形式发送</p>
     * <p>Create Time: 2020/9/2 16:59</p>
     */
    public String doPost(String url) {
        return doPost(url, null, null);
    }

    /**
     * <p>Title: 发送POST请求</p>
     * <p>Description: 请求参数以application/x-www-form-urlencoded形式发送</p>
     * <p>Create Time: 2020/9/2 16:59</p>
     */
    public String doPost(String url, Map<String, String> params) {
        return doPost(url, params, null);
    }

    /**
     * <p>Title: 发送POST请求</p>
     * <p>Description: MIME-Type为application/x-www-form-urlencoded</p>
     * <p>Create Time: 2020/9/2 16:59</p>
     */
    public String doPost(String url, Map<String, String> params, Map<String, String> headers) {
        LOGGER.info("请求类型: <POST>, 请求URL: <{}>", url);
        Request.Builder requestBuilder = new Request.Builder().url(url);
        // 请求头设置
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            LOGGER.info("请求头（值为null的会被过滤）: <{}>", headers.toString());
            this.removeEmptyValue(headers);
            requestBuilder.headers(Headers.of(headers));
        }
        // 请求体设置
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (Objects.nonNull(params) && !params.isEmpty()) {
            LOGGER.info("请求体（值为null的会被过滤）: <{}>", params.toString());
            this.removeEmptyValue(params);
            params.forEach(formBodyBuilder :: add);
        }
        return this.send(requestBuilder.post(formBodyBuilder.build()).build());
    }

    /**
     * <p>Title: 发送文件</p>
     * <p>Description: MIME-Type由调用方指定</p>
     * <p>Create Time: 2020/9/2 17:01</p>
     */
    public String postFile(String url, String mediaTypeStr, File file, Map<String, String> headers) {
        LOGGER.info("发送文件, 请求URL: <{}>", url);
        Objects.requireNonNull(file, "file参数不能为null！");
        Request.Builder requestBuilder = new Request.Builder().url(url);
        // 请求头设置
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            LOGGER.info("请求头（值为null的会被过滤）: <{}>", headers.toString());
            this.removeEmptyValue(headers);
            requestBuilder.headers(Headers.of(headers));
        }
        LOGGER.info("MIME-type: <{}>, 文件路径名称: <{}>, 文件大小: <{}>字节", mediaTypeStr, file.getPath(), file.length());
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.get(mediaTypeStr), file))
                .build();
        return this.send(requestBuilder.post(requestBody).build());
    }

    /**
     * <p>Title: 发送文本</p>
     * <p>Description: MIME-Type为text/plain</p>
     * <p>Create Time: 2020/9/2 17:04</p>
     */
    public String postString(String url, String text, Map<String, String> headers) {
        LOGGER.info("发送文本, 请求URL: <{}>", url);
        if (Objects.isNull(text) || text.isEmpty()) throw new NullPointerException("请求体文本不能为空！");
        return postHandler(url, headers, TEXT_PLAIN_UTF8, text);
    }

    /**
     * <p>Title: 发送json格式的文本</p>
     * <p>Description: MIME-Type为application/json</p>
     * <p>Create Time: 2020/9/2 17:06</p>
     */
    public String postJson(String url, String jsonStr, Map<String, String> headers) {
        LOGGER.info("发送json, 请求URL: <{}>", url);
        if (Objects.isNull(jsonStr) || jsonStr.isEmpty()) throw new NullPointerException("请求体json不能为空！");
        return postHandler(url, headers, APPLICATION_JSON_UTF8, jsonStr);
    }

    private String postHandler(String url, Map<String, String> headers, String mediaTypeStr, String requestBodyStr) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        // 请求头设置
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            LOGGER.info("请求头（值为null的会被过滤）: <{}>", headers.toString());
            this.removeEmptyValue(headers);
            requestBuilder.headers(Headers.of(headers));
        }
        LOGGER.info("MIME-type: <{}>, 请求体: <{}>", mediaTypeStr, requestBodyStr);
        return this.send(requestBuilder.post(RequestBody.create(MediaType.get(mediaTypeStr), requestBodyStr)).build());
    }

    /**
     * <p>Title: 移除value为null的参数</p>
     * <p>Create Time: 2020/6/19 19:58</p>
     */
    private void removeEmptyValue(Map<String, String> map) {
        if (Objects.isNull(map) || map.isEmpty()) return;
        map.entrySet().removeIf(entry -> Objects.isNull(entry.getValue()));
    }

    /**
     * <p>Title: 本工具类真正发送请求的方法</p>
     * <p>Description: 设计为public，调用者也可根据需求自定义Request对象参数</p>
     * <p>Create Time: 2020/8/11 17:56</p>
     */
    public String send(Request request) {
        try (Response response = this.okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException(response.toString());
            }
            String result = response.body().string();
            LOGGER.info("HTTP请求成功！response is: <{}>, response body is <{}>", response.toString(), result);
            return result;
        } catch (Exception e) {
            LOGGER.error("HTTP请求失败！<{}>", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
