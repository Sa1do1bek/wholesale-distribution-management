//package com.wholesale.service;
//
//import com.wholesale.dto.CreateOrderRequest;
//import com.wholesale.dto.OrderItemDTO;
//import com.wholesale.entity.*;
//import com.wholesale.exception.BadRequestException;
//import com.wholesale.exception.ResourceNotFoundException;
//import com.wholesale.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
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
//class OrderServiceTest {
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @Mock
//    private OrderItemRepository orderItemRepository;
//
//    @Mock
//    private CustomerRepository customerRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private InventoryRepository inventoryRepository;
//
//    @InjectMocks
//    private OrderService orderService;
//
//    private Customer customer;
//    private Product product;
//    private Inventory inventory;
//    private Order order;
//    private CreateOrderRequest createOrderRequest;
//
//    @BeforeEach
//    void setUp() {
//        customer = new Customer();
//        customer.setId(1L);
//        customer.setCompanyName("Test Company");
//        customer.setActive(true);
//
//        product = new Product();
//        product.setId(1L);
//        product.setSku("PROD001");
//        product.setName("Test Product");
//        product.setWholesalePrice(new BigDecimal("100.00"));
//        product.setActive(true);
//
//        inventory = new Inventory();
//        inventory.setId(1L);
//        inventory.setProduct(product);
//        inventory.setQuantityOnHand(100);
//        inventory.setQuantityReserved(0);
//
//        order = new Order();
//        order.setId(1L);
//        order.setOrderNumber("ORD-001");
//        order.setCustomer(customer);
//        order.setStatus(Order.OrderStatus.PENDING);
//        order.setSubtotal(new BigDecimal("200.00"));
//        order.setTaxAmount(new BigDecimal("16.00"));
//        order.setShippingCost(new BigDecimal("10.00"));
//        order.setTotalAmount(new BigDecimal("226.00"));
//        order.setCreatedAt(LocalDateTime.now());
//
//        OrderItemDTO itemDTO = new OrderItemDTO();
//        itemDTO.setProductId(1L);
//        itemDTO.setQuantity(2);
//
//        createOrderRequest = new CreateOrderRequest();
//        createOrderRequest.setCustomerId(1L);
//        createOrderRequest.setItems(List.of(itemDTO));
//        createOrderRequest.setShippingAddress("123 Ship St");
//        createOrderRequest.setShippingCity("Ship City");
//        createOrderRequest.setShippingState("SS");
//        createOrderRequest.setShippingZipCode("12345");
//        createOrderRequest.setShippingCountry("USA");
//    }
//
//    @Test
//    void createOrder_ValidData_ReturnsOrder() {
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
//        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
//            Order savedOrder = invocation.getArgument(0);
//            savedOrder.setId(1L);
//            savedOrder.setOrderNumber("ORD-001");
//            return savedOrder;
//        });
//
//        var result = orderService.createOrder(createOrderRequest);
//
//        assertNotNull(result);
//        verify(orderRepository).save(any(Order.class));
//        verify(inventoryRepository).save(any(Inventory.class));
//    }
//
//    @Test
//    void createOrder_CustomerNotFound_ThrowsException() {
//        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(createOrderRequest));
//    }
//
//    @Test
//    void createOrder_InsufficientStock_ThrowsException() {
//        inventory.setQuantityOnHand(1);
//        inventory.setQuantityReserved(0);
//
//        OrderItemDTO itemDTO = new OrderItemDTO();
//        itemDTO.setProductId(1L);
//        itemDTO.setQuantity(10);
//        createOrderRequest.setItems(List.of(itemDTO));
//
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
//
//        assertThrows(BadRequestException.class, () -> orderService.createOrder(createOrderRequest));
//    }
//
//    @Test
//    void getOrderById_ExistingId_ReturnsOrder() {
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//        var result = orderService.getOrderById(1L);
//
//        assertNotNull(result);
//        assertEquals("ORD-001", result.getOrderNumber());
//    }
//
//    @Test
//    void getOrderById_NonExistingId_ThrowsException() {
//        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(999L));
//    }
//
//    @Test
//    void updateOrderStatus_ValidTransition_UpdatesStatus() {
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//
//        var result = orderService.updateOrderStatus(1L, Order.OrderStatus.CONFIRMED);
//
//        assertNotNull(result);
//        verify(orderRepository).save(any(Order.class));
//    }
//
//    @Test
//    void cancelOrder_PendingOrder_CancelsSuccessfully() {
//        OrderItem orderItem = new OrderItem();
//        orderItem.setProduct(product);
//        orderItem.setQuantity(2);
//        order.setItems(List.of(orderItem));
//
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//
//        assertDoesNotThrow(() -> orderService.cancelOrder(1L));
//        verify(orderRepository).save(any(Order.class));
//    }
//
//    @Test
//    void cancelOrder_ShippedOrder_ThrowsException() {
//        order.setStatus(Order.OrderStatus.SHIPPED);
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//        assertThrows(BadRequestException.class, () -> orderService.cancelOrder(1L));
//    }
//}
