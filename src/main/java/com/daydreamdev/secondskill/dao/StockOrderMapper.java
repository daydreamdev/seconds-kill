package com.daydreamdev.secondskill.dao;

import com.daydreamdev.secondskill.pojo.StockOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @auther G.Fukang
 * @date 6/7 14:32
 */
@Mapper
public interface StockOrderMapper {

    int insertSelective(StockOrder order);
}
