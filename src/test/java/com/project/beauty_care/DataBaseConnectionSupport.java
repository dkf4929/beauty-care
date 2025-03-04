package com.project.beauty_care;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
public abstract class DataBaseConnectionSupport {
//    static Network network = Network.newNetwork();

    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("beauty_care")
            .withUsername("root")
            .withPassword("qwer1234")
//            .withNetwork(network)
            .withNetworkAliases("beauty_care");

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", () -> mysqlContainer.getJdbcUrl());
        dynamicPropertyRegistry.add("spring.datasource.username", () -> mysqlContainer.getUsername());
        dynamicPropertyRegistry.add("spring.datasource.password", () -> mysqlContainer.getPassword());
        dynamicPropertyRegistry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }
}
