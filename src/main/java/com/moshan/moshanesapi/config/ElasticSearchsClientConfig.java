package com.moshan.moshanesapi.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: ElasticSearchsClientConfig
 * @Package: com.moshan.moshanesapi.config
 * @Description:
 * @Datetime: 2021/4/10 16:04
 * @Author: zyc
 * @Version: 1.0
 */
//魔山的Spring两步骤
//1.找对象
//2.放到spring中待用
//3.如果是springboot 就先分析源码！
// spinrg源码就两个操作  xxxxAutoConfiguration   xxxxProperties

//相当与spring的ml
@Configuration
public class ElasticSearchsClientConfig {
    // id 是方法名
    // class 是返回值
    // spring <beans id="restHighLevelClient" class="RestHighLevelClient">
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.23.10", 9200, "http")
                        //,new HttpHost("localhost", 9201, "http")     如果是集群可以配置多个
                ));
        return client;
    }
}
