package com.wholesale.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wholesale.dto.CustomerDTO;
import com.wholesale.dto.PageResponse;
import com.wholesale.security.JwtTokenProvider;
import com.wholesale.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setCompanyName("Test Company");
        customerDTO.setFullName("John Doe");
        customerDTO.setEmail("john@test.com");
        customerDTO.setPhone("123-456-7890");
        customerDTO.setAddress("123 Test St");
        customerDTO.setCity("Test City");
        customerDTO.setCountry("USA");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCustomers_ReturnsPageOfCustomers() throws Exception {
        PageResponse<CustomerDTO> pageResponse = new PageResponse<>(
                List.of(customerDTO), 0, 10, 1, 1, true, true
        );
        when(customerService.getAllCustomers(anyInt(), anyInt(), anyString(), anyString())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/customers")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].companyName").value("Test Company"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCustomerById_ExistingId_ReturnsCustomer() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(customerDTO);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Test Company"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCustomer_ValidData_ReturnsCreatedCustomer() throws Exception {
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(customerDTO);

        mockMvc.perform(post("/api/customers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("Test Company"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCustomer_ValidData_ReturnsUpdatedCustomer() throws Exception {
        when(customerService.updateCustomer(eq(1L), any(CustomerDTO.class))).thenReturn(customerDTO);

        mockMvc.perform(put("/api/customers/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Test Company"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCustomer_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/customers/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void searchCustomers_ReturnsMatchingCustomers() throws Exception {
        PageResponse<CustomerDTO> pageResponse = new PageResponse<>(
                List.of(customerDTO), 0, 10, 1, 1, true, true
        );
        when(customerService.searchCustomers(anyString(), anyInt(), anyInt())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/customers/search")
                        .param("q", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].companyName").value("Test Company"));
    }

    @Test
    void getAllCustomers_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized());
    }
}
