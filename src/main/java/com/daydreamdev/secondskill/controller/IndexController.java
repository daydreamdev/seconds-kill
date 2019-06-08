package com.daydreamdev.secondskill.controller;

import com.daydreamdev.secondskill.common.Limit.RedisLimit;
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
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return res == 1 ? success : error;
    }
}
