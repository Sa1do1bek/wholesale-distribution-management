package com.wholesale.repository;

import com.wholesale.entity.Customer;
import com.wholesale.entity.Order;
import com.wholesale.entity.OrderItem;
import com.wholesale.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindByOrderId() {
        Customer c = customerRepository.save(Customer.builder().fullName("C1").email("c1@e.com").build());
        Order o = orderRepository.save(Order.builder().orderNumber("O1").customer(c).status(Order.OrderStatus.PENDING).totalAmount(BigDecimal.TEN).build());
        Product p = productRepository.save(Product.builder().name("P1").sku("S1").category("C1").price(BigDecimal.ONE).build());

        OrderItem oi1 = OrderItem.builder().order(o).product(p).quantity(2).unitPrice(BigDecimal.ONE).subtotal(new BigDecimal("2")).build();
        OrderItem oi2 = OrderItem.builder().order(o).product(p).quantity(3).unitPrice(BigDecimal.ONE).subtotal(new BigDecimal("3")).build();
        orderItemRepository.saveAll(List.of(oi1, oi2));

        List<OrderItem> items = orderItemRepository.findByOrderId(o.getId());
        assertEquals(2, items.size());
    }

    @Test
    void testGetTotalQuantitySoldByProduct() {
        Customer c = customerRepository.save(Customer.builder().fullName("C2").email("c2@e.com").build());
        Order o1 = orderRepository.save(Order.builder().orderNumber("O2").customer(c).status(Order.OrderStatus.PENDING).totalAmount(BigDecimal.TEN).build());
        Order o2 = orderRepository.save(Order.builder().orderNumber("O3").customer(c).status(Order.OrderStatus.PENDING).totalAmount(BigDecimal.TEN).build());
        Product p = productRepository.save(Product.builder().name("P2").sku("S2").category("C1").price(BigDecimal.ONE).build());

        OrderItem oi1 = OrderItem.builder().order(o1).product(p).quantity(5).unitPrice(BigDecimal.ONE).subtotal(new BigDecimal("5")).build();
        OrderItem oi2 = OrderItem.builder().order(o2).product(p).quantity(10).unitPrice(BigDecimal.ONE).subtotal(new BigDecimal("10")).build();
        orderItemRepository.saveAll(List.of(oi1, oi2));

        Long totalSold = orderItemRepository.getTotalQuantitySoldByProduct(p.getId());
        assertEquals(15L, totalSold);
    }
}
