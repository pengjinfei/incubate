package com.pengjinfei.incubate.redis;

import com.pengjinfei.incubate.ServerApplication;
import com.pengjinfei.incubate.dto.UserDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerApplication.class)
public class RedisServiceTest {

	@Autowired
	private RedisService redisService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Test
	public void setIfNotExists() {
	}

	@Test
	public void removeIfEquals() {
	}

	@Test
	public void transferList2Zset() throws InterruptedException {
		String zk = "testZKey";
		String lk = "testLKey";
		redisTemplate.delete(zk);
		redisTemplate.delete(lk);
		redisService.transferZset2List(zk,lk,1,1);
		int threadNum = 10;
		int everyThread = 3;
		List<String> testValues = new LinkedList<String>();
		for (int i = 0; i < threadNum * everyThread; i++) {
			String s = RandomStringUtils.randomAlphabetic(10);
			testValues.add(s);
			redisTemplate.opsForZSet().add(zk, s, RandomUtils.nextDouble(1,100));
		}
		CyclicBarrier barrier = new CyclicBarrier(threadNum);
		CountDownLatch latch = new CountDownLatch(threadNum);
		for (int i = 0; i < threadNum; i++) {
			new Thread(() -> {
				try {
					barrier.await();
					redisService.transferZset2List(zk, lk, everyThread, threadNum*everyThread);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}finally {
				    latch.countDown();
                }
			}).start();
		}
		latch.await();
		int size = Math.toIntExact(redisTemplate.opsForList().size(lk));
		Assert.assertEquals(size,everyThread*threadNum);
	}

	@Test
	public void pollMultiFromZset() throws InterruptedException {
		String key = "zsetTest";
		int threadNum = 3;
		int everyThread = 3;
		redisTemplate.delete(key);
		ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
		int userNum = 1;
		Random random = new Random();
		ConcurrentHashMap<Integer, UserDTO> maps = new ConcurrentHashMap<>(threadNum*everyThread);
		ConcurrentHashMap<Integer, UserDTO> resMaps = new ConcurrentHashMap<>(threadNum*everyThread);
		for (int i = 0; i < threadNum; i++) {
			for (int j = 0; j < everyThread; j++) {
				UserDTO userDTO = new UserDTO(userNum, userNum);
				maps.put(userNum, userDTO);
				userNum++;
				zSetOperations.add(key, userDTO,random.nextDouble()*100);
			}
		}
		CyclicBarrier barrier = new CyclicBarrier(threadNum);
		CountDownLatch latch = new CountDownLatch(threadNum);
		for (int i = 0; i < threadNum; i++) {
			new Thread(() -> {
				try {
					barrier.await();
					List<UserDTO> users = redisService.pollMultiFromZset(key, everyThread, UserDTO.class);
					Assert.assertEquals(users.size(),everyThread);
					users.forEach(userDTO -> resMaps.put(userDTO.getId(),userDTO));
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					latch.countDown();
				}
			}).start();
		}
		latch.await(3, TimeUnit.SECONDS);
		maps.forEach((id, userDTO) -> {
			Assert.assertTrue(resMaps.containsKey(id));
		});
	}
}