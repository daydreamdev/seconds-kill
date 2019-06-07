package com.daydreamdev.secondskill.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @auther G.Fukang
 * @date 6/7 10:37
 */
@Getter
@Setter
public class StockOrder {

    private Integer id;

    private Integer sid;

    private String name;

    private Date createTime;
}
