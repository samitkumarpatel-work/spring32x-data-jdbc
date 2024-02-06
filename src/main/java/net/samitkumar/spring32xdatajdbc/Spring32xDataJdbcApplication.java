package net.samitkumar.spring32xdatajdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringBootApplication
@Slf4j
public class Spring32xDataJdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(Spring32xDataJdbcApplication.class, args);
	}

	@Bean
	RouterFunction<ServerResponse> routerFunction(DepartmentService departmentService, AddressService addressService, EmployeeService employeeService) {
		return RouterFunctions
				.route()
				.path("/department", builder -> builder
						.GET("", serverRequest -> {
							return ServerResponse
									.ok()
									.body(Flux.fromIterable(departmentService.findAll()), Department.class);
						})
						.GET("/{id}", serverRequest -> {
							var id = Integer.parseInt(serverRequest.pathVariable("id"));
							return Mono.fromCallable(() -> departmentService
											.findById(id)
											.orElseThrow(() -> new DataNotFoundException("department not found"))
									)
									.flatMap(ServerResponse.ok()::bodyValue);
						})
						.POST("", serverRequest -> {
							return serverRequest
									.bodyToMono(Department.class)
									.map(departmentService::save)
									.flatMap(ServerResponse.ok()::bodyValue);

						}))
						.PUT("/{id}", serverRequest -> {
							return serverRequest
									.bodyToMono(Department.class)
									.map(department -> new Department(Integer.parseInt(serverRequest.pathVariable("id")), department.name()))
									.map(departmentService::update)
									.flatMap(ServerResponse.ok()::bodyValue);
						})
						.DELETE("/{id}", serverRequest -> {
							departmentService.deleteById(Integer.parseInt(serverRequest.pathVariable("id")));
							return ServerResponse.ok().build();
						})
				.path("/address", builder -> builder
						.GET("", request -> {
							return ServerResponse
									.ok()
									.body(Flux.fromIterable(addressService.findAll()), Address.class);
						})
						.GET("/{id}", request -> {
							var id = Integer.parseInt(request.pathVariable("id"));
							return Mono.fromCallable(() -> addressService
									.findById(id)
									.orElseThrow(() -> new DataNotFoundException("Address not found")))
									.flatMap(ServerResponse.ok()::bodyValue);
						})
						.POST("", request -> {
							return request
									.bodyToMono(Address.class)
									.map(addressService::save)
									.flatMap(ServerResponse.ok()::bodyValue);
						})
						.PUT("/{id}", request -> {
							var id = Integer.parseInt(request.pathVariable("id"));
							return request
									.bodyToMono(Address.class)
									.map(address -> new Address(id, address.location()))
									.map(addressService::update)
									.flatMap(ServerResponse.ok()::bodyValue);
						})
						.DELETE("/{id}", request -> {
							var id = Integer.parseInt(request.pathVariable("id"));
							addressService.deleteById(id);
							return ServerResponse.ok().build();
						})
				)
				.path("/employee", builder -> builder
						.GET("", request -> {
							return ServerResponse
									.ok()
									.body(Flux.fromIterable(employeeService.findAll()), Employee.class);
						})
						.GET("/{id}", request -> {
							var id = Integer.parseInt(request.pathVariable("id"));
							return Mono.fromCallable(() -> employeeService
											.findById(id)
											.orElseThrow(() -> new DataNotFoundException("Employee not found"))
									)
									.flatMap(ServerResponse.ok()::bodyValue);
						})
						.POST("", request -> {
							return request
									.bodyToMono(Employee.class)
									.map(employeeService::save)
									.flatMap(ServerResponse.ok()::bodyValue);
						})
						.PUT("/{id}", request -> {
							var id = Integer.parseInt(request.pathVariable("id"));
							return request
									.bodyToMono(Employee.class)
									.map(employee -> new Employee(id, employee.name(), employee.department(), employee.address()))
									.map(employeeService::update)
									.flatMap(ServerResponse.ok()::bodyValue);
						})
						.DELETE("/{id}", request -> {
							var id = Integer.parseInt(request.pathVariable("id"));
							return ServerResponse.ok().build();
						})
				)
				.after((request, response) -> {
					log.info("{} {} {}",request.method(), request.path(), response.statusCode());
					return response;
				})
				.build();
	}

	private Mono<ServerResponse> problemDetail(HttpStatus httpStatus, String message) {
		return ServerResponse
				.status(httpStatus)
				.bodyValue(ProblemDetail.forStatus(httpStatus));
	}
}

//Exception handler
@ResponseStatus(HttpStatus.NOT_FOUND)
class DataNotFoundException extends RuntimeException {
	public DataNotFoundException() {
		super();
	}
	public DataNotFoundException(String message) {
		super(message);
	}
}

//Entity
record Department(@Id Integer id, String name) {}
record Address(@Id Integer id, String location) {}
record Employee(@Id Integer id, String name, Integer department, Integer address){}

//Repository
interface DepartmentRepository extends ListCrudRepository<Department, Integer> {}
interface AddressRepository extends ListCrudRepository<Address, Integer> {}
interface EmployeeRepository extends ListCrudRepository<Employee, Integer> {}

//Service
interface DepartmentService {
	List<Department> findAll();
	Optional<Department> findById(Integer id);
	Department save(Department department);
	Department update(Department department);
	void deleteById(Integer id);
}

interface AddressService {
	List<Address> findAll();
	Optional<Address> findById(Integer id);
	Address save(Address address);
	Address update(Address address);
	void deleteById(Integer id);
}

interface EmployeeService {
	List<Employee> findAll();
	Optional<Employee> findById(Integer id);
	Employee save(Employee employee);
	Employee update(Employee employee);
	void deleteById(Integer id);
}

@Service
@RequiredArgsConstructor
class DepartmentServiceImpl implements DepartmentService {
	private final DepartmentRepository departmentRepository;

	@Override
	public List<Department> findAll() {
		return departmentRepository.findAll();
	}

	@Override
	public Optional<Department> findById(Integer id) {
		return departmentRepository.findById(id);
	}

	@Override
	public Department save(Department department) {
		Assert.isNull(department.id(), "id should be null for POST request");
		return departmentRepository.save(department);
	}

	@Override
	public Department update(Department department) {
		return departmentRepository.save(department);
	}

	@Override
	public void deleteById(Integer id) {
		departmentRepository.deleteById(id);
	}
}

@Service
@RequiredArgsConstructor
class AddressServiceImpl implements AddressService {
	private final AddressRepository addressRepository;

	@Override
	public List<Address> findAll() {
		return addressRepository.findAll();
	}

	@Override
	public Optional<Address> findById(Integer id) {
		return addressRepository.findById(id);
	}

	@Override
	public Address save(Address address) {
		Assert.isNull(address.id(), "id should be null for POST request");
		return addressRepository.save(address);
	}

	@Override
	public Address update(Address address) {
		return addressRepository.save(address);
	}

	@Override
	public void deleteById(Integer id) {
		addressRepository.deleteById(id);
	}
}

@Service
@RequiredArgsConstructor
class EmployeeServiceImpl implements EmployeeService {
	private final EmployeeRepository employeeRepository;

	@Override
	public List<Employee> findAll() {
		return employeeRepository.findAll();
	}

	@Override
	public Optional<Employee> findById(Integer id) {
		return employeeRepository.findById(id);
	}

	@Override
	public Employee save(Employee employee) {
		Assert.isNull(employee.id(), "id should be null for POST request");
		return employeeRepository.save(employee);
	}

	@Override
	public Employee update(Employee employee) {
		return employeeRepository.save(employee);
	}

	@Override
	public void deleteById(Integer id) {
		employeeRepository.deleteById(id);
	}
}