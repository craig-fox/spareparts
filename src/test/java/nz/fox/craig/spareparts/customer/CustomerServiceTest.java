package nz.fox.craig.spareparts.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nz.fox.craig.spareparts.customer.dto.CustomerRequest;
import nz.fox.craig.spareparts.customer.dto.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@InjectMocks
	private CustomerService customerService;

	@Test
	void createCustomer() {
		CustomerRequest request = new CustomerRequest("Jane Doe", "jane@example.com", "123 Main St");
		Customer saved = Customer.builder()
				.id(1L)
				.name("Jane Doe")
				.email("jane@example.com")
				.address("123 Main St")
				.build();

		when(customerRepository.save(any(Customer.class))).thenReturn(saved);

		CustomerResponse response = customerService.createCustomer(request);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.name()).isEqualTo("Jane Doe");
		assertThat(response.email()).isEqualTo("jane@example.com");
		assertThat(response.address()).isEqualTo("123 Main St");
		verify(customerRepository).save(any(Customer.class));
	}

	@Test
	void getCustomer() {
		Customer customer = Customer.builder()
				.id(1L)
				.name("Jane Doe")
				.email("jane@example.com")
				.address("123 Main St")
				.build();

		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

		CustomerResponse response = customerService.getCustomer(1L);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.name()).isEqualTo("Jane Doe");
	}

	@Test
	void getCustomerNotFound() {
		when(customerRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> customerService.getCustomer(99L))
				.isInstanceOf(CustomerNotFoundException.class)
				.hasMessage("Customer not found with id: 99");
	}

	@Test
	void updateCustomer() {
		CustomerRequest request = new CustomerRequest("Jane Smith", "jane.smith@example.com", "456 Oak Ave");
		Customer existing = Customer.builder()
				.id(1L)
				.name("Jane Doe")
				.email("jane@example.com")
				.address("123 Main St")
				.build();
		Customer updated = Customer.builder()
				.id(1L)
				.name("Jane Smith")
				.email("jane.smith@example.com")
				.address("456 Oak Ave")
				.build();

		when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(customerRepository.save(existing)).thenReturn(updated);

		CustomerResponse response = customerService.updateCustomer(1L, request);

		assertThat(response.name()).isEqualTo("Jane Smith");
		assertThat(response.email()).isEqualTo("jane.smith@example.com");
		assertThat(response.address()).isEqualTo("456 Oak Ave");
	}

	@Test
	void updateCustomerNotFound() {
		CustomerRequest request = new CustomerRequest("Jane Smith", "jane.smith@example.com", "456 Oak Ave");

		when(customerRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> customerService.updateCustomer(99L, request))
				.isInstanceOf(CustomerNotFoundException.class)
				.hasMessage("Customer not found with id: 99");
	}

}
