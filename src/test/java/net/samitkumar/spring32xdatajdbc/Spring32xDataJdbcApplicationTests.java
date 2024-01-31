package net.samitkumar.spring32xdatajdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

@SpringBootTest
@Testcontainers
class Spring32xDataJdbcApplicationTests {

	@Container
	@ServiceConnection
	final static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(PostgreSQLContainer.IMAGE)
			.withInitScript("schema.sql");

	@Test
	void contextLoads() {
	}

	@Autowired
	DepartmentService departmentService;

	@Autowired
	AddressService addressService;

	@Autowired
	EmployeeService employeeService;


	@Test
	void testEmployeeService() {
		Department department = new Department(null, "IT");
		department = departmentService.save(department);
		System.out.println(department);

		Address address = new Address(null, "Bangalore");
		address = addressService.save(address);
		System.out.println(address);

		Employee employee = new Employee(null, "Samit", department.id(), address.id());
		employee = employeeService.save(employee);
		System.out.println(employee);


	}

}
