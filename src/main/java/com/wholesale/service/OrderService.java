package com.wholesale.service;

import com.wholesale.dto.CreateOrderRequest;
import com.wholesale.dto.OrderDTO;
import com.wholesale.dto.OrderItemDTO;
import com.wholesale.dto.PageResponse;
import com.wholesale.entity.*;
import com.wholesale.exception.BadRequestException;
import com.wholesale.exception.ResourceNotFoundException;
import com.wholesale.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    
    private static final AtomicLong orderCounter = new AtomicLong(System.currentTimeMillis());
    
    public PageResponse<OrderDTO> getAllOrders(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Order> orderPage = orderRepository.findAll(pageable);
        
        return buildPageResponse(orderPage);
    }
    
    public PageResponse<OrderDTO> getOrdersByStatus(Order.OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findByStatus(status, pageable);
        return buildPageResponse(orderPage);
    }
    
    public PageResponse<OrderDTO> getOrdersByCustomer(Long customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findByCustomerId(customerId, pageable);
        return buildPageResponse(orderPage);
    }
    
    public PageResponse<OrderDTO> searchOrders(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.searchOrders(search, pageable);
        return buildPageResponse(orderPage);
    }
    
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return OrderDTO.fromEntity(order);
    }
    
    public OrderDTO getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        return OrderDTO.fromEntity(order);
    }
    
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request, String username) {
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));
        
        User user = null;
        if (username != null) {
            user = userRepository.findByUsername(username).orElse(null);
        }
        
        String orderNumber = generateOrderNumber();
        
        Order order = Order.builder()
            .orderNumber(orderNumber)
            .customer(customer)
            .status(Order.OrderStatus.PENDING)
            .totalAmount(BigDecimal.ZERO)
            .shippingAddress(request.getShippingAddress() != null 
                ? request.getShippingAddress() 
                : customer.getAddress())
            .notes(request.getNotes())
            .orderDate(LocalDateTime.now())
            .createdBy(user)
            .build();
        
        order = orderRepository.save(order);
        
        for (OrderItemDTO itemDTO : request.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDTO.getProductId()));
            
            // Check inventory
            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", product.getId()));
            
            if (inventory.getQuantity() < itemDTO.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName() + 
                    ". Available: " + inventory.getQuantity());
            }
            
            // Deduct from inventory
            inventory.setQuantity(inventory.getQuantity() - itemDTO.getQuantity());
            inventoryRepository.save(inventory);
            
            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(itemDTO.getQuantity())
                .unitPrice(product.getPrice())
                .subtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())))
                .build();
            
            order.addItem(orderItem);
        }
        
        order.recalculateTotal();
        order = orderRepository.save(order);
        
        return OrderDTO.fromEntity(order);
    }
    
    @Transactional
    public OrderDTO updateOrderStatus(Long id, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        validateStatusTransition(order.getStatus(), newStatus);
        
        order.setStatus(newStatus);
        
        if (newStatus == Order.OrderStatus.SHIPPED) {
            order.setShippedDate(LocalDateTime.now());
        } else if (newStatus == Order.OrderStatus.DELIVERED) {
            order.setDeliveredDate(LocalDateTime.now());
        } else if (newStatus == Order.OrderStatus.CANCELLED) {
            // Restore inventory
            for (OrderItem item : order.getItems()) {
                Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId())
                    .orElse(null);
                if (inventory != null) {
                    inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
                    inventoryRepository.save(inventory);
                }
            }
        }
        
        order = orderRepository.save(order);
        return OrderDTO.fromEntity(order);
    }
    
    @Transactional
    public OrderDTO addItemToOrder(Long orderId, OrderItemDTO itemDTO) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BadRequestException("Cannot modify order that is not in PENDING status");
        }
        
        Product product = productRepository.findById(itemDTO.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDTO.getProductId()));
        
        Inventory inventory = inventoryRepository.findByProductId(product.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", product.getId()));
        
        if (inventory.getQuantity() < itemDTO.getQuantity()) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName());
        }
        
        inventory.setQuantity(inventory.getQuantity() - itemDTO.getQuantity());
        inventoryRepository.save(inventory);
        
        OrderItem orderItem = OrderItem.builder()
            .order(order)
            .product(product)
            .quantity(itemDTO.getQuantity())
            .unitPrice(product.getPrice())
            .subtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())))
            .build();
        
        order.addItem(orderItem);
        order = orderRepository.save(order);
        
        return OrderDTO.fromEntity(order);
    }
    
    public Long getOrderCountByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
    
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.getTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    
    public BigDecimal getMonthlyRevenue() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime now = LocalDateTime.now();
        BigDecimal revenue = orderRepository.getRevenueByDateRange(startOfMonth, now);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    
    private String generateOrderNumber() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = orderCounter.incrementAndGet() % 10000;
        return String.format("ORD-%s-%04d", datePrefix, sequence);
    }
    
    private void validateStatusTransition(Order.OrderStatus current, Order.OrderStatus target) {
        if (current == Order.OrderStatus.CANCELLED || current == Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot change status of " + current + " order");
        }
        
        if (current == Order.OrderStatus.PENDING && 
            target != Order.OrderStatus.PROCESSING && 
            target != Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("PENDING order can only be moved to PROCESSING or CANCELLED");
        }
        
        if (current == Order.OrderStatus.PROCESSING && 
            target != Order.OrderStatus.SHIPPED && 
            target != Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("PROCESSING order can only be moved to SHIPPED or CANCELLED");
        }
        
        if (current == Order.OrderStatus.SHIPPED && target != Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("SHIPPED order can only be moved to DELIVERED");
        }
    }
    
    private PageResponse<OrderDTO> buildPageResponse(Page<Order> page) {
        return PageResponse.<OrderDTO>builder()
            .content(page.getContent().stream().map(OrderDTO::fromEntity).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }
}
