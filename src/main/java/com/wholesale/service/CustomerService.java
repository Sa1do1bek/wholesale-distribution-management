package com.wholesale.service;

import com.wholesale.dto.CustomerDTO;
import com.wholesale.dto.PageResponse;
import com.wholesale.entity.Customer;
import com.wholesale.exception.DuplicateResourceException;
import com.wholesale.exception.ResourceNotFoundException;
import com.wholesale.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public PageResponse<CustomerDTO> getAllCustomers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        
        return buildPageResponse(customerPage);
    }
    
    public PageResponse<CustomerDTO> getActiveCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        Page<Customer> customerPage = customerRepository.findByActiveTrue(pageable);
        
        return buildPageResponse(customerPage);
    }
    
    public PageResponse<CustomerDTO> searchCustomers(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        Page<Customer> customerPage = customerRepository.searchCustomers(search, pageable);
        
        return buildPageResponse(customerPage);
    }
    
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return CustomerDTO.fromEntity(customer);
    }
    
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        if (customerDTO.getEmail() != null && customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
        }
        
        Customer customer = customerDTO.toEntity();
        customer.setActive(true);
        customer = customerRepository.save(customer);
        
        return CustomerDTO.fromEntity(customer);
    }
    
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        if (customerDTO.getEmail() != null && !customerDTO.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(customerDTO.getEmail())) {
                throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
            }
        }
        
        customer.setFullName(customerDTO.getFullName());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
        customer.setAddress(customerDTO.getAddress());
        customer.setCompanyName(customerDTO.getCompanyName());
        customer.setCity(customerDTO.getCity());
        customer.setCountry(customerDTO.getCountry());
        
        customer = customerRepository.save(customer);
        return CustomerDTO.fromEntity(customer);
    }
    
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        customer.setActive(false);
        customerRepository.save(customer);
    }
    
    @Transactional
    public void permanentlyDeleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer", "id", id);
        }
        customerRepository.deleteById(id);
    }
    
    private PageResponse<CustomerDTO> buildPageResponse(Page<Customer> page) {
        return PageResponse.<CustomerDTO>builder()
            .content(page.getContent().stream().map(CustomerDTO::fromEntity).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }
}
