package com.project.beauty_care;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class RedisConnectionSupport extends DataBaseConnectionSupport {
    protected static final GenericContainer<?> REDIS_CONTAINER;
    private static final int REDIS_PORT = 6379;

    static {
        REDIS_CONTAINER = new GenericContainer<>("redis:latest")
                .withExposedPorts(REDIS_PORT)
                .withReuse(Boolean.TRUE)
                .waitingFor(Wait.forListeningPort());

        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        dynamicPropertyRegistry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT).toString());
    }
}
