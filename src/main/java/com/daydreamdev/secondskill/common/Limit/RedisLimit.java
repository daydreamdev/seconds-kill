package com.daydreamdev.secondskill.common.limit;

import com.daydreamdev.secondskill.common.utils.RedisPool;
import com.daydreamdev.secondskill.common.utils.ScriptUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * @auther G.Fukang
 * @date 6/7 21:45
 */
@Slf4j
public class RedisLimit {

    private static final int FAIL_CODE = 0;

    private static Integer limit = 5;

    /**
     * Redis 限流
     */
    public static Boolean limit() {
        Jedis jedis = null;
        Object result = null;
        try {
            // 获取 jedis 实例
            jedis = RedisPool.getJedis();
            // 解析 Lua 文件
            String script = ScriptUtil.getScript("limit.lua");
            // 请求限流
            String key = String.valueOf(System.currentTimeMillis() / 1000);
            // 计数限流
            result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limit)));
            if (FAIL_CODE != (Long) result) {
                log.info("成功获取令牌");
                return true;
            }
        } catch (Exception e) {
            log.error("limit 获取 Jedis 实例失败：", e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
        return false;
    }
}
