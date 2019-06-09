# 如何设计一个秒杀系统

## 系统的特点

- 高性能：秒杀涉及大量的并发读和并发写，因此支持高并发访问这点非常关键
- 一致性：秒杀商品减库存的实现方式同样关键，有限数量的商品在同一时刻被很多倍的请求同时来减库存，在大并发更新的过程中都要保证数据的准确性。
- 高可用：秒杀时会在一瞬间涌入大量的流量，为了避免系统宕机，保证高可用，需要做好流量限制

## 优化思路

- 后端优化：将请求尽量拦截在系统上游
  - 限流：屏蔽掉无用的流量，允许少部分流量走后端。假设现在库存为 10，有 1000 个购买请求，最终只有 10 个可以成功，99% 的请求都是无效请求
  - 削峰：秒杀请求在时间上高度集中于某一个时间点，瞬时流量容易压垮系统，因此需要对流量进行削峰处理，缓冲瞬时流量，尽量让服务器对资源进行平缓处理
  - 异步：将同步请求转换为异步请求，来提高并发量，本质也是削峰处理
  - 利用缓存：创建订单时，每次都需要先查询判断库存，只有少部分成功的请求才会创建订单，因此可以将商品信息放在缓存中，减少数据库查询
  - 负载均衡：利用 Nginx 等使用多个服务器并发处理请求，减少单个服务器压力
- 前端优化：
  - 限流：前端答题或验证码，来分散用户的请求
  - 禁止重复提交：限定每个用户发起一次秒杀后，需等待才可以发起另一次请求，从而减少用户的重复请求
  - 本地标记：用户成功秒杀到商品后，将提交按钮置灰，禁止用户再次提交请求
  - 动静分离：将前端静态数据直接缓存到离用户最近的地方，比如用户浏览器、CDN 或者服务端的缓存中
- 防作弊优化：
  - 隐藏秒杀接口：如果秒杀地址直接暴露，在秒杀开始前可能会被恶意用户来刷接口，因此需要在没到秒杀开始时间不能获取秒杀接口，只有秒杀开始了，才返回秒杀地址 url 和验证 MD5，用户拿到这两个数据才可以进行秒杀
  - 同一个账号多次发出请求：在前端优化的禁止重复提交可以进行优化；也可以使用 Redis 标志位，每个用户的所有请求都尝试在 Redis 中插入一个 `userId_secondsKill` 标志位，成功插入的才可以执行后续的秒杀逻辑，其他被过滤掉，执行完秒杀逻辑后，删除标志位
  - 多个账号一次性发出多个请求：一般这种请求都来自同一个 IP 地址，可以检测 IP 的请求频率，如果过于频繁则弹出一个验证码
  - 多个账号不同 IP 发起不同请求：这种一般都是僵尸账号，检测账号的活跃度或者等级等信息，来进行限制。比如微博抽奖，用 iphone 的年轻女性用户中奖几率更大。通过用户画像限制僵尸号无法参与秒杀或秒杀不能成功

## 代码优化

代码整体思路参考的 [@crossoverJie](<https://github.com/crossoverJie>)，做了以下几点变动

1. 将 SSM 换成 SpringBoot，开箱即用，更加容易上手，将 Mapper 替换为注解，精简了部分代码
2. 原项目中依赖了开发者自己的开源包 [distributed-redis-tool](<https://github.com/crossoverJie/distributed-redis-tool>)，本项目将用到的限流部分直接集成到代码中
3. 摒弃了 Dubbo 和 Zookeeper，减少了代码量，也使得新手更加容易上手
4. 加入缓存预热，在秒杀开始前，将库存信息读到缓存中
5. 缓存更新逻辑中加入 Redis 事务

### Jmeter 压测并发量变化图

// TODO

### 0. 基本秒杀逻辑

```java
@Override
public int createWrongOrder(int sid) throws Exception {
    // 数据库校验库存
    Stock stock = checkStock(sid);
    // 扣库存(无锁)
    saleStock(stock);
    // 生成订单
    int res = createOrder(stock);
    return res;
}
private Stock checkStock(int sid) throws Exception {
    Stock stock = stockService.getStockById(sid);
    if (stock.getCount() < 1) {
        throw new RuntimeException("库存不足");
    }
    return stock;
}
private int saleStock(Stock stock) {
    stock.setSale(stock.getSale() + 1);
    stock.setCount(stock.getCount() - 1);
    return stockService.updateStockById(stock);
}
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
// 扣库存 Mapper 文件
@Update("UPDATE stock SET count = #{count, jdbcType = INTEGER}, name = #{name, jdbcType = 			     VARCHAR}, " + "sale = #{sale,jdbcType = INTEGER},version = #{version,jdbcType = INTEGER} " + "WHERE id = #{id, jdbcType = INTEGER}")
```

### 1. 乐观锁更新库存，解决超卖问题

超卖问题出现的场景

![]()

悲观锁是表级锁，虽然可以解决超卖问题，但是由于排斥外部请求，会导致很多请求等待锁，卡死在这里，如果这种请求很多就会耗尽连接，系统出现异常。乐观锁默认不加锁，跟新失败就直接返回抢购失败，可以承受较高并发

![]()

```java
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
// 乐观锁 Mapper 文件
@Update("UPDATE stock SET count = count - 1, sale = sale + 1, version = version + 1 WHERE " +
        "id = #{id, jdbcType = INTEGER} AND version = #{version, jdbcType = INTEGER}")
```

### 2. Redis 计数限流

根据前面的优化分析，假设现在有 10 个商品

##  数据库建表

```mysql
CREATE TABLE `stock` (
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
    `count` int(11) NOT NULL COMMENT '库存',
    `sale` int(11) NOT NULL COMMENT '已售',
    `version` int(11) NOT NULL COMMENT '乐观锁，版本号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
CREATE TABLE `stock_order` (
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `sid` int(11) NOT NULL COMMENT '库存ID',
    `name` varchar(30) NOT NULL DEFAULT '' COMMENT '商品名称',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8;
```

## 参考

>- [极客时间：许令波 - 如何设计一个秒杀系统](<https://time.geekbang.org/column/intro/127>)
>- [crossoverjie：SSM(十八)秒杀架构实践](<https://crossoverjie.top/2018/05/07/ssm/SSM18-seconds-kill/>)
>- [秒杀系统优化方案（下）吐血整理](<https://www.cnblogs.com/xiangkejin/p/9351501.html>)
>- [电商网站秒杀与抢购的系统架构](http://www.codeceo.com/article/spike-system-artch.html)