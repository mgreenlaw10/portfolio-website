package com.portfolio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "app.data-dir=target/test-data", "app.check-github-on-startup=false" })
class PortfolioWebsiteApplicationTests {

	@Test
	void contextLoads() {
	}

}
