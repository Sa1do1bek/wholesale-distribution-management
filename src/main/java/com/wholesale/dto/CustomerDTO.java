package com.wholesale.dto;

import com.wholesale.entity.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    
    private Long id;
    
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Size(max = 150, message = "Company name must not exceed 150 characters")
    private String companyName;
    
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;
    
    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;
    
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static CustomerDTO fromEntity(Customer customer) {
        return CustomerDTO.builder()
            .id(customer.getId())
            .fullName(customer.getFullName())
            .phone(customer.getPhone())
            .email(customer.getEmail())
            .address(customer.getAddress())
            .companyName(customer.getCompanyName())
            .city(customer.getCity())
            .country(customer.getCountry())
            .active(customer.isActive())
            .createdAt(customer.getCreatedAt())
            .updatedAt(customer.getUpdatedAt())
            .build();
    }
    
    public Customer toEntity() {
        return Customer.builder()
            .id(this.id)
            .fullName(this.fullName)
            .phone(this.phone)
            .email(this.email)
            .address(this.address)
            .companyName(this.companyName)
            .city(this.city)
            .country(this.country)
            .active(this.active)
            .build();
    }
}
