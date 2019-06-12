package com.daydreamdev.secondskill.common.utils;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @auther G.Fukang
 * @date 6/8 10:07
 */
@Slf4j
public class RedisPoolUtil {

    /**
     * 设置 key - value 值
     *
     * @param key
     * @param value
     */
    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
        return result;
    }

    /**
     * 获取 key - value 值
     *
     * @param key
     */
    public static String get(String key) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
        return result;
    }

    /**
     * 删除 key - value 值
     *
     * @param key
     */
    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error", key, e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
        return result;
    }

    /**
     * key - value 自增
     */
    public static Long incr (String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.incr(key);
        } catch (Exception e) {
            log.error("listGet key:{} error", key, e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
        return result;
    }

    /**
     * key - value 自减
     */
    public static Long decr (String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.decr(key);
        } catch (Exception e) {
            log.error("listGet key:{} error", key, e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
        return result;
    }

    /**
     * List - get 操作
     */
    public static List<String> listGet(String key) {
        Jedis jedis = null;
        List<String> result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            log.error("listGet key:{} error", key, e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
        return result;
    }

    /**
     * List - put 操作
     */
    public static Long listPut(String key, String count, String sale, String version) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.lpush(key, version, sale, count);
        } catch (Exception e) {
            log.error("listPut key:{} error", key, e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
        return result;
    }
}
