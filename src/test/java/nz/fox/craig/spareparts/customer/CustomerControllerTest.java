package nz.fox.craig.spareparts.customer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nz.fox.craig.spareparts.customer.dto.CustomerRequest;
import nz.fox.craig.spareparts.customer.dto.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CustomerService customerService;

	@Test
	void createCustomer() throws Exception {
		CustomerRequest request = new CustomerRequest("Jane Doe", "jane@example.com", "123 Main St");
		CustomerResponse response = new CustomerResponse(1L, "Jane Doe", "jane@example.com", "123 Main St");

		when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Jane Doe"))
				.andExpect(jsonPath("$.email").value("jane@example.com"))
				.andExpect(jsonPath("$.address").value("123 Main St"));
	}

	@Test
	void createCustomerWithInvalidEmail() throws Exception {
		CustomerRequest request = new CustomerRequest("Jane Doe", "not-an-email", "123 Main St");

		mockMvc.perform(post("/api/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("email: Email must be a valid email address"));
	}

	@Test
	void createCustomerWithMissingFields() throws Exception {
		mockMvc.perform(post("/api/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getCustomer() throws Exception {
		CustomerResponse response = new CustomerResponse(1L, "Jane Doe", "jane@example.com", "123 Main St");

		when(customerService.getCustomer(1L)).thenReturn(response);

		mockMvc.perform(get("/api/customers/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Jane Doe"));
	}

	@Test
	void getCustomerNotFound() throws Exception {
		when(customerService.getCustomer(99L)).thenThrow(new CustomerNotFoundException(99L));

		mockMvc.perform(get("/api/customers/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Customer not found with id: 99"));
	}

	@Test
	void updateCustomer() throws Exception {
		CustomerRequest request = new CustomerRequest("Jane Smith", "jane.smith@example.com", "456 Oak Ave");
		CustomerResponse response = new CustomerResponse(1L, "Jane Smith", "jane.smith@example.com", "456 Oak Ave");

		when(customerService.updateCustomer(eq(1L), any(CustomerRequest.class))).thenReturn(response);

		mockMvc.perform(put("/api/customers/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Jane Smith"))
				.andExpect(jsonPath("$.email").value("jane.smith@example.com"));
	}

}
