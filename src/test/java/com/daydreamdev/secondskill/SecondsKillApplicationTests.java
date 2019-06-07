package com.daydreamdev.secondskill;

import com.daydreamdev.secondskill.dao.StockMapper;
import com.daydreamdev.secondskill.pojo.Stock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecondsKillApplicationTests {

	@Autowired
	private StockMapper stockMapper;

	@Test
	public void contextLoads() {
		Stock stock = stockMapper.findStockById(1);
		System.out.println("name: " + stock.getName() + " count: " + stock.getCount() + " sale: " + stock.getSale());
	}

}
