package com.github.vakho10;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("GUI tests fail in headless CI")
@SpringBootTest
class JlectronApplicationTests {

	@Test
	void contextLoads() {
	}
}
