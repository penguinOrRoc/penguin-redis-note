package com.penguin.redis.controller;

import com.penguin.redis.conf.RedissonConf;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 高并发缓存超时失效问题
 */
@RestController
public class RedissonController {
    @Autowired
    private  StringRedisTemplate stringRedisTemplate;


    @GetMapping("/redisson")
    public  void redisson() {
        RedissonClient redisson =  RedissonConf.getRedisson();
        System.out.println("信用卡余额："+stringRedisTemplate.opsForValue().get("balance"));
        System.out.println("信用卡账单："+stringRedisTemplate.opsForValue().get("debt"));
        System.out.println("**********交易开始**********");
        String client_id = UUID.randomUUID().toString();

        String lock_key = "user_id";
        RLock redissonLock = redisson.getLock(lock_key);
        try {

            /** 方式1
             * Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lock_key,"penguin");
             * stringRedisTemplate.expire(lock_key,10,TimeUnit.SECONDS);
             */
            /** 方式2
             * boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lock_key,client_id,10,TimeUnit.SECONDS);
            /**
             * if(!result){
             *      System.out.println("锁了");
             * }
             */

            redissonLock.lock();
            int amtTosubstract = 10; //实际刷的钱
            System.out.println("刷卡金额："+amtTosubstract);
            int balance = Integer.parseInt(stringRedisTemplate.opsForValue().get("balance"));
            if (balance > amtTosubstract) {
                stringRedisTemplate.boundValueOps("balance").decrement(amtTosubstract);
                stringRedisTemplate.boundValueOps("debt").increment(amtTosubstract);
                System.out.println("**********您的信用卡消费" + amtTosubstract + "元~**********");
            } else {
                System.out.println("**********您的信用卡余额不足~**********");
            }
        } finally {
            redissonLock.unlock();
//            if(client_id)
        }
        System.out.println("**********交易结束**********");
        System.out.println("信用卡余额："+stringRedisTemplate.opsForValue().get("balance"));
        System.out.println("信用卡账单："+stringRedisTemplate.opsForValue().get("debt"));


    }
}


