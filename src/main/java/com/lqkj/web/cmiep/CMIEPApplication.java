package com.lqkj.web.cmiep;


import com.lqkj.web.cmiep.config.PropertyDeploy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCaching
public class CMIEPApplication {

    public static void main(String[] args) {
        PropertyDeploy deploy=new PropertyDeploy();
        deploy.deploy();
        SpringApplication.run(CMIEPApplication.class, args);
    }
}
