package com.charlie.redis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedisTestContainerIntTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        REDIS_CONTAINER.start();
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    @Test
    public void shouldSaveUser_toRedis() {
        User user = new User(1, "first", "last", "email");
        userRepository.save(user);
        User saved = userRepository.findById(user.getId());
        assertNotNull(saved);
    }

    @Test
    public void verifyLockExpires() throws InterruptedException {
        // will lock for 10 seconds
        userController.lock();
        // user will be saved until the lock ends
        User user = userRepository.findById(9999l);
        assertNotNull(user);

        // sleep for 5 seconds
        Thread.sleep(5000);

        // since its only been 5/10 seconds, the lock should still be in the cache
        User after5Seconds = userRepository.findById(9999l);
        assertNotNull(after5Seconds);

        // sleep for the remaining 5 seconds
        Thread.sleep(5000);

        // now try to get after the 10 seconds, which shouldnt be allowed
        User after10SecondsUser = userRepository.findById(9999l);
        assertNull(after10SecondsUser);
        userRepository.evict(9999L);
    }
}
