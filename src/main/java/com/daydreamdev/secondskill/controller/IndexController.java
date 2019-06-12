package com.daydreamdev.secondskill.controller;

import com.daydreamdev.secondskill.common.limit.RedisLimit;
import com.daydreamdev.secondskill.common.stockWithRedis.StockWithRedis;
import com.daydreamdev.secondskill.service.api.OrderService;
import com.daydreamdev.secondskill.service.api.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @auther G.Fukang
 * @date 6/7 12:32
 */
@Slf4j
@Controller
@RequestMapping(value = "/")
public class IndexController {

    private static final String success = "SUCCESS";
    private static final String error = "ERROR";

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockService stockService;

    /**
     * 压测前先请求该方法，初始化数据库和缓存
     */
    @RequestMapping(value = "initDBAndRedis", method = RequestMethod.POST)
    @ResponseBody
    public String initDBAndRedisBefore(HttpServletRequest request) {
        int res = 0;
        try {
            // 初始化库存信息
            res = stockService.initDBBefore();
            // 清空订单表
            res &= (orderService.delOrderDBBefore() == 0 ? 1 : 0);
            // 重置缓存
            StockWithRedis.initRedisBefore();
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
        if (res == 1) {
            log.info("重置数据库和缓存成功！");
        }
        return res == 1 ? success : error;
    }

    /**
     * 秒杀基本逻辑，存在超卖问题
     *
     * @param sid
     * @return
     */
    @RequestMapping(value = "createWrongOrder", method = RequestMethod.POST)
    @ResponseBody
    public String createWrongOrder(HttpServletRequest request, int sid) {
        int res = 0;
        try {
            res = orderService.createWrongOrder(sid);
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
        return res == 1 ? success : error;
    }

    /**
     * 乐观锁扣库存
     *
     * @param sid
     * @return
     */
    @RequestMapping(value = "createOptimisticOrder", method = RequestMethod.POST)
    @ResponseBody
    public String createOptimisticOrder(HttpServletRequest request, int sid) {
        int res = 0;
        try {
            res = orderService.createOptimisticOrder(sid);
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return res == 1 ? success : error;
    }

    /**
     * 乐观锁更新 + 限流
     *
     * @param sid
     */
    @RequestMapping(value = "createOptimisticLimitOrder", method = RequestMethod.POST)
    @ResponseBody
    public String createOptimisticLimitOrder(HttpServletRequest request, int sid) {
        int res = 0;
        try {
            if (RedisLimit.limit()) {
                res = orderService.createOptimisticOrder(sid);
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return res == 1 ? success : error;
    }

    /**
     * Redis 缓存库存，减少 DB 压力
     * 在 RedisPreheatRunner 做缓存预热，需要 stock.id = 1
     * @param sid
     */
    @RequestMapping(value = "createOrderWithLimitAndRedis", method = RequestMethod.POST)
    @ResponseBody
    public String createOrderWithLimitAndRedis(HttpServletRequest request, int sid) {
        int res = 0;
        try {
            if (RedisLimit.limit()) {
                res = orderService.createOrderWithLimitAndRedis(sid);
                if (res == 1) {
                    log.info("秒杀成功");
                }
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return res == 1 ? success : error;
    }

    /**
     * 限流 + Redis 缓存库存 + KafkaTest 异步下单
     * @param sid
     */
    @RequestMapping(value = "createOrderWithLimitAndRedisAndKafka", method = RequestMethod.POST)
    @ResponseBody
    public String createOrderWithLimitAndRedisAndKafka(HttpServletRequest request, int sid) {
        try {
            if (RedisLimit.limit()) {
                orderService.createOrderWithLimitAndRedisAndKafka(sid);
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return "秒杀请求正在处理，排队中";
    }
}
