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

    private String alipayPublicKey; // 对应 yml 中的 alipay-public-key

}