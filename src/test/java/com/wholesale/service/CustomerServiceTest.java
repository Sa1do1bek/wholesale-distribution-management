//package com.wholesale.service;
//
//import com.wholesale.dto.CustomerDTO;
//import com.wholesale.entity.Customer;
//import com.wholesale.exception.DuplicateResourceException;
//import com.wholesale.exception.ResourceNotFoundException;
//import com.wholesale.repository.CustomerRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CustomerServiceTest {
//
//    @Mock
//    private CustomerRepository customerRepository;
//
//    @InjectMocks
//    private CustomerService customerService;
//
//    private Customer customer;
//    private CustomerDTO customerDTO;
//
//    @BeforeEach
//    void setUp() {
//        customer = new Customer();
//        customer.setId(1L);
//        customer.setCompanyName("Test Company");
//        customer.setContactName("John Doe");
//        customer.setEmail("john@test.com");
//        customer.setPhone("123-456-7890");
//        customer.setAddress("123 Test St");
//        customer.setCity("Test City");
//        customer.setState("TS");
//        customer.setZipCode("12345");
//        customer.setCountry("USA");
//        customer.setCreditLimit(new BigDecimal("10000.00"));
//        customer.setPaymentTerms("NET30");
//        customer.setActive(true);
//        customer.setCreatedAt(LocalDateTime.now());
//        customer.setUpdatedAt(LocalDateTime.now());
//
//        customerDTO = new CustomerDTO();
//        customerDTO.setCompanyName("Test Company");
//        customerDTO.setContactName("John Doe");
//        customerDTO.setEmail("john@test.com");
//        customerDTO.setPhone("123-456-7890");
//        customerDTO.setAddress("123 Test St");
//        customerDTO.setCity("Test City");
//        customerDTO.setState("TS");
//        customerDTO.setZipCode("12345");
//        customerDTO.setCountry("USA");
//        customerDTO.setCreditLimit(new BigDecimal("10000.00"));
//        customerDTO.setPaymentTerms("NET30");
//    }
//
//    @Test
//    void getAllCustomers_ReturnsPageOfCustomers() {
//        Page<Customer> page = new PageImpl<>(List.of(customer));
//        when(customerRepository.findAll(any(PageRequest.class))).thenReturn(page);
//
//        var result = customerService.getAllCustomers(0, 10);
//
//        assertNotNull(result);
//        assertEquals(1, result.getContent().size());
//        verify(customerRepository).findAll(any(PageRequest.class));
//    }
//
//    @Test
//    void getCustomerById_ExistingId_ReturnsCustomer() {
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//
//        var result = customerService.getCustomerById(1L);
//
//        assertNotNull(result);
//        assertEquals("Test Company", result.getCompanyName());
//    }
//
//    @Test
//    void getCustomerById_NonExistingId_ThrowsException() {
//        when(customerRepository.findById(999L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(999L));
//    }
//
//    @Test
//    void createCustomer_ValidData_ReturnsCustomer() {
//        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
//        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
//
//        var result = customerService.createCustomer(customerDTO);
//
//        assertNotNull(result);
//        assertEquals("Test Company", result.getCompanyName());
//        verify(customerRepository).save(any(Customer.class));
//    }
//
//    @Test
//    void createCustomer_DuplicateEmail_ThrowsException() {
//        when(customerRepository.existsByEmail(anyString())).thenReturn(true);
//
//        assertThrows(DuplicateResourceException.class, () -> customerService.createCustomer(customerDTO));
//    }
//
//    @Test
//    void updateCustomer_ExistingId_ReturnsUpdatedCustomer() {
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
//
//        customerDTO.setCompanyName("Updated Company");
//        var result = customerService.updateCustomer(1L, customerDTO);
//
//        assertNotNull(result);
//        verify(customerRepository).save(any(Customer.class));
//    }
//
//    @Test
//    void deleteCustomer_ExistingId_DeletesCustomer() {
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//        doNothing().when(customerRepository).delete(any(Customer.class));
//
//        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));
//        verify(customerRepository).delete(customer);
//    }
//
//    @Test
//    void searchCustomers_ReturnsMatchingCustomers() {
//        when(customerRepository.searchByCompanyNameOrContactName(anyString()))
//                .thenReturn(List.of(customer));
//
//        var result = customerService.searchCustomers("Test");
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//    }
//}
