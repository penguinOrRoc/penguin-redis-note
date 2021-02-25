package com.penguin.redis;

import redis.clients.jedis.*;

import java.util.Set;
/**
 *jedis基础
 */
public class JedisBasicMain {
    private static JedisPool jedisPool = new JedisPool("192.168.43.113", 7001);
    private static Jedis jedis = jedisPool.getResource();
    public static void main(String[] args) throws InterruptedException {
        transactionWatch();
    }

    public static void transactionNormal() {
        Transaction transaction = jedis.multi();//开启事务
        try {
            transaction.hset("tx", "f1", "v1");
            transaction.hset("tx", "f2", "v2");
            transaction.hset("tx", "f3", "v3");
            transaction.exec();//提交事务
        } catch (Exception e) {
            transaction.discard();//取消事务
        }
    }

    public static boolean transactionWatch() throws InterruptedException {
        int balance;//可用余额
        int debt;//欠额
        int amtTosubstract = 10; //实际刷的钱
        jedis.watch("balance");
        System.out.println("**********wait begin**********");
        Thread.sleep(10000);//模拟高并发延迟，让其他线程修改balance
        System.out.println("**********wait end**********");

        balance = Integer.parseInt(jedis.get("balance"));
        if (balance < amtTosubstract) {
            jedis.unwatch();
            System.out.println("**********Canel**********");
            return false;
        } else {
            System.out.println("**********Transaction**********");
            Transaction transaction = jedis.multi();
            transaction.decrBy("balance",amtTosubstract);
            transaction.incrBy("debt",amtTosubstract);
            transaction.exec();
            balance = Integer.parseInt(jedis.get("balance"));
            debt = Integer.parseInt(jedis.get("debt"));
            System.out.println("**********balance**********"+balance);
            System.out.println("**********balance**********"+debt);
            return true;
        }


    }

    public static void getAllKeyType() {
        Set<String> keys = jedis.keys("*");
        System.out.println("KEY\t\t\t\tTYPE");

        for (String key : keys) {
            System.out.println(key + "\t" + jedis.type(key));
        }

    }
}


