package com.lin.wxpaydemo.config;

import com.lin.wxpaydemo.config.value.ApplicationValues;
import com.wechat.pay.contrib.apache.httpclient.Credentials;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.*;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;


@Configuration
public class WxHttpClient {

    @Autowired
    private ApplicationValues appValues;

    @Bean
    public CloseableHttpClient httpClient() throws Exception {
        String merchantId = appValues.getMchId();
        String merchantSerialNumber = appValues.getMchSerialNo();
        PrivateKey privateKey = merchantPrivateKey();
        String apiV3Key = appValues.getApiV3Key();

        // 获取证书管理器实例
        CertificatesManager certificatesManager = CertificatesManager.getInstance();
        // 向证书管理器增加需要自动更新平台证书的商户信息
        certificatesManager.putMerchant(merchantId, new WechatPay2Credentials(merchantId,
                new PrivateKeySigner(merchantSerialNumber, privateKey)), apiV3Key.getBytes(StandardCharsets.UTF_8));
        // 从证书管理器中获取verifier
        Verifier verifier = certificatesManager.getVerifier(merchantId);
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(merchantId, merchantSerialNumber, privateKey)
                .withValidator(new WechatPay2Validator(verifier));
        // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
        CloseableHttpClient httpClient = builder.build();
        return httpClient;
    }

    public PrivateKey merchantPrivateKey() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(appValues.getPrivateKeyPath());
        classPathResource .getFile();
        InputStream inputStream = classPathResource.getInputStream();
        return PemUtil.loadPrivateKey(inputStream);
    }

}
