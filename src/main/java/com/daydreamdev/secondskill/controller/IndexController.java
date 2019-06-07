package com.daydreamdev.secondskill.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @auther G.Fukang
 * @date 6/7 12:32
 */
@Controller
@RequestMapping(value = "/")
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);
}
