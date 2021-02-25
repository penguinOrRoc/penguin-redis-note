package com.penguin.redis.conf;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class RedissonConf {
    //https://github.com/redisson/redisson
    public static RedissonClient getRedisson(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.43.113:7001");
        RedissonClient redisson = Redisson.create(config);
        return  redisson;

    }
}


