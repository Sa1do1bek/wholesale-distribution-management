package com.wholesale.repository;

import com.wholesale.entity.Customer;
import com.wholesale.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer createCustomer(String name) {
        Customer c = Customer.builder()
                .fullName(name)
                .email(name + "@example.com")
                .companyName(name + " Inc")
                .build();
        return customerRepository.save(c);
    }

    private Order createOrder(String number, Customer customer, Order.OrderStatus status, BigDecimal amount) {
        Order o = Order.builder()
                .orderNumber(number)
                .customer(customer)
                .status(status)
                .totalAmount(amount)
                .orderDate(LocalDateTime.now())
                .build();
        return orderRepository.save(o);
    }

    @Test
    void testFindByOrderNumber() {
        Customer c = createCustomer("Alice");
        createOrder("ORD1", c, Order.OrderStatus.PENDING, BigDecimal.TEN);

        Optional<Order> found = orderRepository.findByOrderNumber("ORD1");
        assertTrue(found.isPresent());
        assertEquals(BigDecimal.TEN, found.get().getTotalAmount());
    }

    @Test
    void testFindByStatus() {
        Customer c = createCustomer("Bob");
        createOrder("ORD2", c, Order.OrderStatus.PENDING, BigDecimal.ONE);
        createOrder("ORD3", c, Order.OrderStatus.SHIPPED, BigDecimal.TEN);

        Page<Order> pendingOrders = orderRepository.findByStatus(Order.OrderStatus.PENDING, PageRequest.of(0, 10));
        assertEquals(1, pendingOrders.getTotalElements());
        assertEquals("ORD2", pendingOrders.getContent().get(0).getOrderNumber());
    }

    @Test
    void testGetTotalRevenue() {
        Customer c = createCustomer("Charlie");
        createOrder("ORD4", c, Order.OrderStatus.DELIVERED, new BigDecimal("100.00"));
        createOrder("ORD5", c, Order.OrderStatus.PENDING, new BigDecimal("50.00"));
        createOrder("ORD6", c, Order.OrderStatus.CANCELLED, new BigDecimal("200.00")); // Cancelled, should be excluded

        BigDecimal revenue = orderRepository.getTotalRevenue();
        assertEquals(0, new BigDecimal("150.00").compareTo(revenue));
    }

    @Test
    void testCountByStatus() {
        Customer c = createCustomer("Dave");
        createOrder("ORD7", c, Order.OrderStatus.SHIPPED, BigDecimal.ONE);
        createOrder("ORD8", c, Order.OrderStatus.SHIPPED, BigDecimal.ONE);

        Long count = orderRepository.countByStatus(Order.OrderStatus.SHIPPED);
        assertEquals(2L, count);
    }
}
