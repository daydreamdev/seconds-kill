package com.daydreamdev.secondskill.controller;

import com.daydreamdev.secondskill.service.api.OrderService;
import com.daydreamdev.secondskill.service.api.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Controller
@RequestMapping(value = "/")
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    private static final String success = "SUCCESS";
    private static final String error = "ERROR";

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockService stockService;

    /**
     * 秒杀基本逻辑，存在超卖问题
     * @param sid
     * @return
     */
    @RequestMapping(value = "createWrongOrder", method = RequestMethod.POST)
    @ResponseBody
    public String createWrongOrder(HttpServletRequest request, int sid) {
        logger.info("sid = [{}]", sid);
        int res = 0;
        try {
            res = orderService.createWrongOrder(sid);
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
        return res == 1 ? success : error;
    }

    /**
     * 乐观锁扣库存
     * @param sid
     * @return
     */
    @RequestMapping(value = "createOptimisticOrder", method = RequestMethod.POST)
    @ResponseBody
    public String createOptimisticOrder(HttpServletRequest request, int sid) {
        logger.info("sid = [{}]", sid);
        int res = 0;
        try {
            res = orderService.createOptimisticOrder(sid);
        } catch (Exception e) {
            logger.error("Exception: " + e);
        }
        return res == 1 ? success : error;
    }
}
