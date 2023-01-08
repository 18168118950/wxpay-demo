package com.lin.wxpaydemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lin.wxpaydemo.config.value.ApplicationValues;
import com.lin.wxpaydemo.util.OrderNoUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;


@RestController
public class PayController {

    private static final String JSAPI_URL = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";

    private static final String J5_URL = "https://api.mch.weixin.qq.com/v3/pay/partner/transactions/h5";

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationValues appValues;

    @GetMapping("/jsapi")
    public void jsapiPay() throws Exception {

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("mchid", appValues.getMchId())
                .put("appid", appValues.getAppid())
                .put("description", "一卡通移动端充值")
                .put("notify_url", appValues.getNotifyDomain())
                .put("out_trade_no", OrderNoUtils.getOrderNo());
        rootNode.putObject("amount")
                .put("total", 1);
        rootNode.putObject("payer")
                .put("openid", "owz364jdELAxXrnOLfXkJ0ixyQho");

        invokePostApi(JSAPI_URL, rootNode);
    }

    @GetMapping("/h5")
    public String pay() throws Exception {

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("sp_mchid", appValues.getMchId())
                .put("out_trade_no", OrderNoUtils.getOrderNo())
                .put("sp_appid", appValues.getAppid())
                .put("description", "一卡通移动端充值")
                .put("notify_url", appValues.getNotifyDomain());
        rootNode.putObject("amount")
                .put("total", 1);
        rootNode.putObject("scene_info")
                .put("payer_client_ip", "127.0.0.1")
                .putObject("h5_info")
                .put("type", "Wap");
        String resultStr = invokePostApi(J5_URL, rootNode);
        return resultStr;

    }

    public String invokePostApi(String url, ObjectNode rootNode) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        objectMapper.writeValue(bos, rootNode);
        httpPost.setEntity(new StringEntity(bos.toString("UTF-8"), "UTF-8"));
        CloseableHttpResponse response = httpClient.execute(httpPost);
        String bodyAsString = EntityUtils.toString(response.getEntity());
        return bodyAsString;
    }

}
