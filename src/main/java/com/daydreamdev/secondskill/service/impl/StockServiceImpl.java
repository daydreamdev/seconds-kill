package com.daydreamdev.secondskill.service.impl;

import com.daydreamdev.secondskill.pojo.Stock;
import com.daydreamdev.secondskill.service.api.StockService;
import org.springframework.stereotype.Service;

/**
 * @auther G.Fukang
 * @date 6/7 12:45
 */
@Service(value = "StockService")
public class StockServiceImpl implements StockService {

    @Override
    public int getStockCount(int id) {
        return 0;
    }

    @Override
    public Stock getStockById(int id) {
        return null;
    }

    @Override
    public int updateStockById(Stock stock) {
        return 0;
    }
}
