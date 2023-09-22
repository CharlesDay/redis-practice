package com.charlie.redis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestRedisConfiguration.class)
@ActiveProfiles("test")
public class RedisIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    @Test
    @DisableOnMac
    @DisableOnWindows
    public void shouldSaveUser_toRedis() {
        User user = new User(1, "first", "last", "email");
        userRepository.save(user);
        User saved = userRepository.findById(user.getId());

        assertNotNull(saved);
    }

    @Test
    @DisableOnMac
    @DisableOnWindows
    public void verifyLockExpires() throws InterruptedException {
        System.out.println("Starting int test" + System.getenv("SPRING_PROFILES_ACTIVE"));
        // will lock for 1 minute
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
    }
}