package cn.solarcat.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisClusterTest {
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Test
	public void addItemID() {
		redisTemplate.opsForValue().set("jkghkg", "kingdee");
	}
}
