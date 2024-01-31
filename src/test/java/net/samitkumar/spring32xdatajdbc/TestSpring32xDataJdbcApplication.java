package net.samitkumar.spring32xdatajdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestSpring32xDataJdbcApplication {

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(PostgreSQLContainer.IMAGE)
				.withInitScript("schema.sql");
	}

	public static void main(String[] args) {
		SpringApplication.from(Spring32xDataJdbcApplication::main).with(TestSpring32xDataJdbcApplication.class).run(args);
	}

}
