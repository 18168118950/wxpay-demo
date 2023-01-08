package com.lin.wxpaydemo;

import com.lin.wxpaydemo.config.value.ApplicationValues;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WxpayDemoApplicationTests {

	@Autowired
	private ApplicationValues appValues;

	@Test
	void contextLoads() {

		System.out.println(appValues);
	}

}
