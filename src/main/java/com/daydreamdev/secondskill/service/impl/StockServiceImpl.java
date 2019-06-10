package com.daydreamdev.secondskill.service.impl;

import com.daydreamdev.secondskill.dao.StockMapper;
import com.daydreamdev.secondskill.pojo.Stock;
import com.daydreamdev.secondskill.service.api.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @auther G.Fukang
 * @date 6/7 12:45
 */
@Service(value = "StockService")
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    @Override
    public int getStockCount(int id) {
        Stock stock = stockMapper.selectByPrimaryKey(id);
        return stock.getCount();
    }

    @Override
    public Stock getStockById(int id) {

        return stockMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateStockById(Stock stock) {

        return stockMapper.updateByPrimaryKeySelective(stock);
    }

    @Override
    public int updateStockByOptimistic(Stock stock) {

        return stockMapper.updateByOptimistic(stock);
    }

    @Override
    public int initDBBefore() {

        return stockMapper.initDBBefore();
    }
}
