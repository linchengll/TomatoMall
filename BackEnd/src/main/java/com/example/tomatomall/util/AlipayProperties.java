package com.example.tomatomall.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

    private String alipayPublicKey;
    private String serverUrl;
    private String appId;
    private String privateKey;
    private String notifyUrl;

}