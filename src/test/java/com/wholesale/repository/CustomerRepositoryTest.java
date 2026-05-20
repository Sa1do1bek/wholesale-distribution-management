package com.wholesale.repository;

import com.wholesale.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindByEmail() {
        Customer customer = Customer.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .companyName("Doe Inc.")
                .build();
        customerRepository.save(customer);

        Optional<Customer> found = customerRepository.findByEmail("john@example.com");
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getFullName());
    }

    @Test
    void testExistsByEmail() {
        Customer customer = Customer.builder()
                .fullName("Jane Doe")
                .email("jane@example.com")
                .companyName("Jane LLC")
                .build();
        customerRepository.save(customer);

        assertTrue(customerRepository.existsByEmail("jane@example.com"));
        assertFalse(customerRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testFindByActiveTrue() {
        Customer activeCustomer = Customer.builder()
                .fullName("Active User")
                .email("active@example.com")
                .active(true)
                .build();
        customerRepository.save(activeCustomer);

        Customer inactiveCustomer = Customer.builder()
                .fullName("Inactive User")
                .email("inactive@example.com")
                .active(false)
                .build();
        customerRepository.save(inactiveCustomer);

        Page<Customer> result = customerRepository.findByActiveTrue(PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
        assertEquals("Active User", result.getContent().get(0).getFullName());
    }

    @Test
    void testSearchCustomers() {
        Customer customer1 = Customer.builder()
                .fullName("Alice Smith")
                .email("alice@example.com")
                .companyName("Tech Corp")
                .phone("1234567890")
                .build();
        customerRepository.save(customer1);

        Customer customer2 = Customer.builder()
                .fullName("Bob Jones")
                .email("bob@example.com")
                .companyName("Food Inc")
                .phone("0987654321")
                .build();
        customerRepository.save(customer2);

        Page<Customer> searchFullName = customerRepository.searchCustomers("alice", PageRequest.of(0, 10));
        assertEquals(1, searchFullName.getTotalElements());

        Page<Customer> searchCompanyName = customerRepository.searchCustomers("Food", PageRequest.of(0, 10));
        assertEquals(1, searchCompanyName.getTotalElements());
    }

    @Test
    void testFindByCityAndCountry() {
        Customer customer = Customer.builder()
                .fullName("City User")
                .email("city@example.com")
                .city("New York")
                .country("USA")
                .build();
        customerRepository.save(customer);

        Page<Customer> cityResult = customerRepository.findByCity("New York", PageRequest.of(0, 10));
        assertEquals(1, cityResult.getTotalElements());

        Page<Customer> countryResult = customerRepository.findByCountry("USA", PageRequest.of(0, 10));
        assertEquals(1, countryResult.getTotalElements());
    }
}
