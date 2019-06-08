package com.daydreamdev.secondskill.common;

import com.daydreamdev.secondskill.common.utils.RedisPoolUtil;
import com.daydreamdev.secondskill.pojo.Stock;
import com.daydreamdev.secondskill.service.api.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @auther G.Fukang
 * @date 6/8 13:41
 * 缓存预热，在 Spring Boot 启动后立即执行此方法
 */
@Component
public class RedisPreheatRunner implements ApplicationRunner {

    @Autowired
    private StockService stockService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 从数据库中查询热卖商品
        Stock stock = stockService.getStockById(1);
        // 删除 Redis 中旧的缓存
        RedisPoolUtil.del(RedisKeysConstant.STOCK + stock.getId());
        // 缓存预热
        RedisPoolUtil.listPut(RedisKeysConstant.STOCK + stock.getId(), String.valueOf(stock.getCount()),
                String.valueOf(stock.getSale()), String.valueOf(stock.getVersion()));
    }
}
