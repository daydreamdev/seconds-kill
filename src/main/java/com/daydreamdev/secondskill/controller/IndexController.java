package com.daydreamdev.secondskill.controller;

import com.daydreamdev.secondskill.service.api.OrderService;
import com.daydreamdev.secondskill.service.api.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @auther G.Fukang
 * @date 6/7 12:32
 */
@Controller
@RequestMapping(value = "/")
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockService stockService;

    @RequestMapping("/createWrongOrder/{sid}")
    @ResponseBody
    public String createWrongOrder(@PathVariable int sid) {
        logger.info("sid = [{}]", sid);
        int id = 0;
        try {
            id = orderService.createWrongOrder(sid);
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
        return String.valueOf(id);
    }
}
