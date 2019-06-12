package com.daydreamdev.secondskill.common.stockWithRedis;

import com.daydreamdev.secondskill.common.utils.RedisPool;
import com.daydreamdev.secondskill.common.utils.RedisPoolUtil;
import com.daydreamdev.secondskill.pojo.Stock;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * @auther G.Fukang
 * @date 6/8 21:47
 */
@Slf4j
public class StockWithRedis {

    /**
     * Redis 事务保证库存更新
     * 捕获异常后应该删除缓存
     */
    public static void updateStockWithRedis(Stock stock) {
        Jedis jedis = null;
        try {
            jedis = RedisPool.getJedis();
            // 开始事务
            Transaction transaction = jedis.multi();
            // 事务操作
            RedisPoolUtil.decr(RedisKeysConstant.STOCK_COUNT + stock.getId());
            RedisPoolUtil.incr(RedisKeysConstant.STOCK_SALE + stock.getId());
            RedisPoolUtil.incr(RedisKeysConstant.STOCK_VERSION + stock.getId());
            // 结束事务
            List<Object> list = transaction.exec();
        } catch (Exception e) {
            log.error("updateStock 获取 Jedis 实例失败：", e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
    }

    /**
     * 重置缓存
     */
    public static void initRedisBefore() {
        Jedis jedis = null;
        try {
            jedis = RedisPool.getJedis();
            // 开始事务
            Transaction transaction = jedis.multi();
            // 事务操作
            RedisPoolUtil.set(RedisKeysConstant.STOCK_COUNT + 1, "50");
            RedisPoolUtil.set(RedisKeysConstant.STOCK_SALE + 1, "0");
            RedisPoolUtil.set(RedisKeysConstant.STOCK_VERSION + 1, "0");
            // 结束事务
            List<Object> list = transaction.exec();
        } catch (Exception e) {
            log.error("initRedis 获取 Jedis 实例失败：", e);
        } finally {
            RedisPool.jedisPoolClose(jedis);
        }
    }
}
