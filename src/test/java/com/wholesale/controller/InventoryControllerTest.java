package com.wholesale.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wholesale.dto.InventoryDTO;
import com.wholesale.dto.PageResponse;
import com.wholesale.security.JwtTokenProvider;
import com.wholesale.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private InventoryDTO inventoryDTO;

    @BeforeEach
    void setUp() {
        inventoryDTO = new InventoryDTO();
        inventoryDTO.setId(1L);
        inventoryDTO.setProductId(1L);
        inventoryDTO.setProductName("Test Product");
        inventoryDTO.setQuantity(50);
        inventoryDTO.setMinQuantity(10);
        inventoryDTO.setWarehouseLocation("Main Warehouse");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllInventory() throws Exception {
        PageResponse<InventoryDTO> pageResponse = new PageResponse<>(
                List.of(inventoryDTO), 0, 10, 1, 1, true, true
        );
        when(inventoryService.getAllInventory(anyInt(), anyInt(), anyString(), anyString())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetInventoryById() throws Exception {
        when(inventoryService.getInventoryById(1L)).thenReturn(inventoryDTO);

        mockMvc.perform(get("/api/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateInventory() throws Exception {
        when(inventoryService.createInventory(any(InventoryDTO.class))).thenReturn(inventoryDTO);

        mockMvc.perform(post("/api/inventory")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE") // create requires ADMIN or MANAGER
    void testCreateInventory_Forbidden() throws Exception {
        mockMvc.perform(post("/api/inventory")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventoryDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateInventory() throws Exception {
        when(inventoryService.updateInventory(eq(1L), any(InventoryDTO.class))).thenReturn(inventoryDTO);

        mockMvc.perform(put("/api/inventory/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(50));
    }
}
