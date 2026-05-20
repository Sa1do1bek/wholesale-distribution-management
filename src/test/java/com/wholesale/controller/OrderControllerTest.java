package com.wholesale.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wholesale.dto.CreateOrderRequest;
import com.wholesale.dto.OrderDTO;
import com.wholesale.dto.PageResponse;
import com.wholesale.entity.Order;
import com.wholesale.security.JwtTokenProvider;
import com.wholesale.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private OrderDTO orderDTO;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setOrderNumber("ORD1");
        orderDTO.setCustomerId(1L);
        orderDTO.setStatus("PENDING");
        orderDTO.setTotalAmount(new BigDecimal("100.00"));
        orderDTO.setOrderDate(LocalDateTime.now());

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(1L);
        createOrderRequest.setShippingAddress("123 Test St");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllOrders() throws Exception {
        PageResponse<OrderDTO> pageResponse = new PageResponse<>(
                List.of(orderDTO), 0, 10, 1, 1, true, true
        );
        when(orderService.getAllOrders(anyInt(), anyInt(), anyString(), anyString())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetOrderById() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateOrder() throws Exception {
        when(orderService.createOrder(any(CreateOrderRequest.class), anyString())).thenReturn(orderDTO);

        mockMvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("ORD1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus() throws Exception {
        orderDTO.setStatus("SHIPPED");
        when(orderService.updateOrderStatus(eq(1L), any(Order.OrderStatus.class))).thenReturn(orderDTO);

        mockMvc.perform(patch("/api/orders/1/status")
                        .with(csrf())
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }
}
