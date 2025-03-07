package com.project.beauty_care;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class DataBaseConnectionSupport {
    private static final Network network = Network.newNetwork();
    protected static final MySQLContainer<?> mysqlContainer;

    static {
        mysqlContainer = new MySQLContainer<>("mysql:8.0")
                .withDatabaseName("beauty_care")
                .withUsername("root")
                .withPassword("qwer1234")
                .withNetwork(network)
                .withEnv("DOCKER_HOST", "unix:///var/run/docker.sock")
                .withNetworkAliases("beauty_care");

        mysqlContainer.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", mysqlContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mysqlContainer::getPassword);
    }
}
