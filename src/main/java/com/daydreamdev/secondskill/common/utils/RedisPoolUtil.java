package com.daydreamdev.secondskill.common.utils;

import com.daydreamdev.secondskill.common.RedisKeysConstant;
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
     * 判断 key - value 是否存在
     */
    public static Boolean exists(String key) {
        Jedis jedis = null;
        Boolean result = false;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.exists(key);
        } catch (Exception e) {
            log.error("exists key:{} value:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }

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
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
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
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
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
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
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
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
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
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        int sid = 111;
        RedisPoolUtil.listPut(RedisKeysConstant.STOCK + sid, "10", "0", "0");
        List<String> data = RedisPoolUtil.listGet(RedisKeysConstant.STOCK + sid);
        Integer count = Integer.parseInt(data.get(0));
        Integer sale = Integer.parseInt(data.get(1));
        Integer version = Integer.parseInt(data.get(2));
        System.out.println(count.toString() + "  " + sale.toString() + "  " + version.toString());
    }
}
