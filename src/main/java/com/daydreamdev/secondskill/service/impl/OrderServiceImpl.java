package com.daydreamdev.secondskill.service.impl;

import com.daydreamdev.secondskill.common.RedisKeysConstant;
import com.daydreamdev.secondskill.common.utils.RedisPoolUtil;
import com.daydreamdev.secondskill.dao.StockOrderMapper;
import com.daydreamdev.secondskill.pojo.Stock;
import com.daydreamdev.secondskill.pojo.StockOrder;
import com.daydreamdev.secondskill.service.api.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @auther G.Fukang
 * @date 6/7 12:44
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service(value = "OrderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private StockServiceImpl stockService;

    @Autowired
    private StockOrderMapper orderMapper;

    @Override
    public int createWrongOrder(int sid) throws Exception {
        Stock stock = checkStock(sid);
        saleStock(stock);
        int id = createOrder(stock);

        return id;
    }

    @Override
    public int createOptimisticOrder(int sid) throws Exception {
        // 校验库存
        Stock stock = checkStock(sid);
        // 乐观锁更新
        saleStockOptimstic(stock);
        // 创建订单
        int id = createOrder(stock);

        return id;
    }

    @Override
    public int createOrderWithLimitAndRedis(int sid) throws Exception {
        // 校验库存，从 Redis 中获取
        Stock stock = checkStockWithRedis(sid);
        // 乐观锁更新库存和Redis
        saleStockOptimsticWithRedis(stock);
        // 创建订单
        int id = createOrder(stock);

        return id;
    }

    /**
     * Redis 中校验库存（存在少卖问题）
     * 由于更新数据时，从 Redis Stock 删除，因此存在校验时 Redis 数据为 NULL 的情况
     *
     * @param sid
     */
    private Stock checkStockWithRedis(int sid) throws Exception {
        Integer count = null;
        Integer sale = null;
        Integer version = null;
        Boolean res = RedisPoolUtil.exists(RedisKeysConstant.STOCK + sid);
        if (!res) {
            // Redis 不存在，先从数据库中获取，再放到 Redis 中
            Stock newStock = stockService.getStockById(sid);
            RedisPoolUtil.listPut(RedisKeysConstant.STOCK + newStock.getId(), String.valueOf(newStock.getCount()),
                    String.valueOf(newStock.getSale()), String.valueOf(newStock.getVersion()));
            count = newStock.getCount();
            sale = newStock.getSale();
            version = newStock.getVersion();
        } else {
            List<String> data = RedisPoolUtil.listGet(RedisKeysConstant.STOCK + sid);
            if (data.size() == 0 || data.isEmpty()) {
                log.error("此处报错**************************");
            }
            count = Integer.parseInt(data.get(0));
            sale = Integer.parseInt(data.get(1));
            version = Integer.parseInt(data.get(2));
        }
        if (count <= 0) {
            log.info("库存不足");
            throw new RuntimeException("库存不足 Redis currentCount: " + sale);
        }
        Stock stock = new Stock();
        stock.setId(sid);
        stock.setCount(count);
        stock.setSale(sale);
        stock.setVersion(version);
        // 此处应该是热更新，但是在数据库中只有一个商品，所以直接赋值
        stock.setName("手机");

        return stock;
    }

    /**
     * 更新数据库和 Redis 库存
     * 要保证缓存和 DB 的一致性
     */
    private void saleStockOptimsticWithRedis(Stock stock) throws Exception {
        // 乐观锁更新数据库
        int res = stockService.updateStockByOptimistic(stock);
        if (res == 0) {
            throw new RuntimeException("并发更新库存失败");
        }
        // 删除缓存，应该使用 Redis 事务
        RedisPoolUtil.del(RedisKeysConstant.STOCK + stock.getId());
/*        // 从数据库中查询
        Stock newStock = stockService.getStockById(stock.getId());
        // 重新放入缓存，应该使用 Redis 事务
        RedisPoolUtil.listPut(RedisKeysConstant.STOCK + newStock.getId(), String.valueOf(newStock.getCount()),
                String.valueOf(newStock.getSale()), String.valueOf(newStock.getVersion()));*/
    }

    /**
     * 校验库存
     */
    private Stock checkStock(int sid) throws Exception {
        Stock stock = stockService.getStockById(sid);
        if (stock.getCount() <= 0) {
            throw new RuntimeException("库存不足");
        }
        return stock;
    }

    /**
     * 扣库存
     */
    private int saleStock(Stock stock) {
        stock.setSale(stock.getSale() + 1);
        stock.setCount(stock.getCount() - 1);
        return stockService.updateStockById(stock);
    }

    /**
     * 乐观锁扣库存
     */
    private void saleStockOptimstic(Stock stock) throws Exception {
        int count = stockService.updateStockByOptimistic(stock);
        if (count == 0) {
            throw new RuntimeException("并发更新库存失败");
        }
    }

    /**
     * 创建订单
     */
    private int createOrder(Stock stock) throws Exception {
        StockOrder order = new StockOrder();
        order.setSid(stock.getId());
        order.setName(stock.getName());
        order.setCreateTime(new Date());
        int res = orderMapper.insertSelective(order);
        if (res == 0) {
            throw new RuntimeException("创建订单失败");
        }
        return res;
    }
}
