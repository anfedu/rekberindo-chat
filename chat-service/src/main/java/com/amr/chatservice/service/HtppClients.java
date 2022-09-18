package com.amr.chatservice.service;

import com.amr.chatservice.model.UserSummary;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class HtppClients {
    private static final int TIMEOUT = 60;
    private static final Logger logger = LoggerFactory.getLogger(HtppClients.class);

//    @Value("${flipapisecret}")
//    private String apiSecret;

    public String post(final String logId, final String host, final String endpoint,List<NameValuePair> urlParameters) throws IOException {

        try (final CloseableHttpClient client = HttpClients.createDefault()) {

            System.out.println(host);

            logger.debug("{} host : {}", logId, host);
            logger.debug("{} endpoint : {}", logId, endpoint);
            RequestConfig.Builder requestConfig = RequestConfig.custom();
            requestConfig.setConnectTimeout(TIMEOUT * 1000);
            requestConfig.setConnectionRequestTimeout(TIMEOUT * 1000);
            requestConfig.setSocketTimeout(TIMEOUT * 1000);
            String auth= Base64.getEncoder().encodeToString(("apiSecret"+":").getBytes());
            HttpPost httpPost = new HttpPost(host + endpoint );
            httpPost.setConfig(requestConfig.build());
            System.out.println(auth);
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Authorization", "Basic " + auth);
            httpPost.setHeader("idempotency-key", logId);

            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
            logger.debug("{} url final : {}", logId, endpoint );
            JSONObject sk=new JSONObject();
            for (NameValuePair pair : urlParameters) {
                sk.put(pair.getName(),pair.getValue());

            }
            logger.debug("{} request body : {}", logId, sk.toString());

            CloseableHttpResponse response = client.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();

            String contentType = MediaType.TEXT_PLAIN_VALUE;

            String content;
            if (null == response.getEntity().getContentType()) {
                content = response.getStatusLine().getReasonPhrase();
            } else {
                contentType = response.getEntity().getContentType().getValue();
                content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
            logger.debug("{} response body : {}", logId, content);

            return content;

        } catch (SocketTimeoutException ste) {
            throw new SocketTimeoutException("timeout when waiting response from the host");
        } catch (ConnectTimeoutException cte) {
            throw new ConnectTimeoutException("timeout when create connection to the host");
        } catch (IOException e) {
            throw e;
        }
    }

    public String get(final String logId, final String host, final String endpoint) throws IOException {

        try (final CloseableHttpClient client = HttpClients.createDefault()) {

            System.out.println(host);

            logger.debug("{} host : {}", logId, host);
            logger.debug("{} endpoint : {}", logId, endpoint);
            RequestConfig.Builder requestConfig = RequestConfig.custom();
            requestConfig.setConnectTimeout(TIMEOUT * 1000);
            requestConfig.setConnectionRequestTimeout(TIMEOUT * 1000);
            requestConfig.setSocketTimeout(TIMEOUT * 1000);
            String auth= Base64.getEncoder().encodeToString((":").getBytes());
            HttpGet httpGet = new HttpGet(host + endpoint );
            httpGet.setConfig(requestConfig.build());
            System.out.println(auth);
            httpGet.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpGet.setHeader("Authorization", "Basic " + auth);
            logger.debug("{} url final : {}", logId, endpoint );
            System.out.println("request-body: \nContent-type:application/x-www-form-urlencoded\nAuthorization:"+auth);
            logger.debug("{} request body : {}", logId);
            java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
            java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
            System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
            System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
            CloseableHttpResponse response = client.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();

            String contentType = MediaType.TEXT_PLAIN_VALUE;

            String content;
            if (null == response.getEntity().getContentType()) {
                content = response.getStatusLine().getReasonPhrase();
            } else {
                contentType = response.getEntity().getContentType().getValue();
                content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
            logger.debug("{} response body : {}", logId, content);

            return content;

        } catch (SocketTimeoutException ste) {
            throw new SocketTimeoutException("timeout when waiting response from the host");
        } catch (ConnectTimeoutException cte) {
            throw new ConnectTimeoutException("timeout when create connection to the host");
        } catch (IOException e) {
            throw e;
        }
    }

    public String postToZenzivaWA(final String logId, final String host, final String endpoint,String to,String message) throws IOException {
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("userkey", "be39d1ee06b7"));
        urlParameters.add(new BasicNameValuePair("passkey", "43eac8aa99278e5544d3ec40"));
        urlParameters.add(new BasicNameValuePair("to", to));
        urlParameters.add(new BasicNameValuePair("message", message));
        try (final CloseableHttpClient client = HttpClients.createDefault()) {

            System.out.println(host);

            logger.debug("{} host : {}", logId, host);
            logger.debug("{} endpoint : {}", logId, endpoint);
            RequestConfig.Builder requestConfig = RequestConfig.custom();
            requestConfig.setConnectTimeout(TIMEOUT * 1000);
            requestConfig.setConnectionRequestTimeout(TIMEOUT * 1000);
            requestConfig.setSocketTimeout(TIMEOUT * 1000);
            HttpPost httpPost = new HttpPost(host + endpoint );
            httpPost.setConfig(requestConfig.build());

            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
            logger.debug("{} url final : {}", logId, endpoint );
            JSONObject sk=new JSONObject();
            for (NameValuePair pair : urlParameters) {
                sk.put(pair.getName(),pair.getValue());

            }
            logger.debug("{} request body : {}", logId, sk.toString());

            CloseableHttpResponse response = client.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();

            String contentType = MediaType.TEXT_PLAIN_VALUE;

            String content;
            if (null == response.getEntity().getContentType()) {
                content = response.getStatusLine().getReasonPhrase();
            } else {
                contentType = response.getEntity().getContentType().getValue();
                content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
            logger.debug("{} response body : {}", logId, content);

            return content;

        } catch (SocketTimeoutException ste) {
            throw new SocketTimeoutException("timeout when waiting response from the host");
        } catch (ConnectTimeoutException cte) {
            throw new ConnectTimeoutException("timeout when create connection to the host");
        } catch (IOException e) {
            throw e;
        }
    }


    public UserSummary postUsers( String id,String token) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = null;
        UserSummary userSummary= null;

        try {

            HttpPost request = new HttpPost("http://localhost:8001/get-user/"+id);
            JSONObject body = new JSONObject().put("referenceNo",""+new Date().getTime());

//            body.put("signature", signature);
            StringEntity entity2 = new StringEntity(body.toString());
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String signature = OgpCrypto.getSignature(body.toString()+time, "rekber!");
            // add request headers
            request.addHeader("token", token);
            request.addHeader("signature", signature);
            request.addHeader("transmission-date-time", time);

            request.setEntity(entity2);
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                // Get HttpResponse Status
                logger.debug(response.getProtocolVersion()+"");              // HTTP/1.1
                logger.debug(response.getStatusLine().getStatusCode()+"");   // 200
                logger.debug(response.getStatusLine().getReasonPhrase()+""); // OK
                logger.debug(response.getStatusLine().toString()+"");        // HTTP/1.1 200 OK

                logger.debug("{} request body : {}", id, token);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String

                    try {
                        result = EntityUtils.toString(entity);
                        logger.debug("Response : {}",result);
                        JSONObject userJson= new JSONObject(result);

                        userSummary= UserSummary.builder().id(userJson.getString("id"))
                                .phoneNumber(userJson.getString("phoneNumber"))
                                .email(userJson.get("email").toString())
                                .noKtp(userJson.get("noKtp").toString())
                                .address(userJson.get("address").toString())
                                .name(userJson.get("name").toString())
                                .profileImg(userJson.get("profileImg").toString())
                                .role(userJson.get("role").toString())
                                .build();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            } finally {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            httpClient.close();
        }
        return userSummary;
    }
}
