package com.daydreamdev.secondskill.service.api;

import com.daydreamdev.secondskill.pojo.Stock;

/**
 * @auther G.Fukang
 * @date 6/7 12:35
 */
public interface OrderService {

    /**
     * 清空订单表
     */
    int delOrderDBBefore();

    /**
     * 创建订单（存在超卖问题）
     *
     * @param sid
     * @return int
     */
    int createWrongOrder(int sid) throws Exception;

    /**
     * 数据库乐观锁更新库存，解决超卖问题
     *
     * @param sid
     * @return int
     */
    int createOptimisticOrder(int sid) throws Exception;

    /**
     * 数据库乐观锁更新库存，库存查询 Redis 减小数据库读压力
     *
     * @param sid
     * @return int
     */
    int createOrderWithLimitAndRedis(int sid) throws Exception;

    /**
     * 限流 + Redis 缓存库存信息 + KafkaTest 异步发送消息
     *
     * @param sid
     */
    void createOrderWithLimitAndRedisAndKafka(int sid) throws Exception;

    /**
     * Kafka 消费消息
     *
     * @param stock
     */
    int consumerTopicToCreateOrderWithKafka(Stock stock) throws Exception;
}
