package com.tenzo.seckill.service;

import redis.clients.jedis.Jedis;

public class RedisService {
    public Jedis getJedis() {
        return new Jedis("localhost",6379);
    }
}
