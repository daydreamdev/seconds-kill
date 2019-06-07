package com.daydreamdev.secondskill.service.api;

/**
 * @auther G.Fukang
 * @date 6/7 12:35
 */
public interface OrderService {

    /**
     * 创建订单（存在超卖问题）
     * @param sid
     * @return int
     */
    int createWrongOrder(int sid);

    /**
     * 数据库乐观锁更新库存，解决超卖问题
     * @param sid
     * @return int
     */
    int createOptimisticOrder(int sid);
}
