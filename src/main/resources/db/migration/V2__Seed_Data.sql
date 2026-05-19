-- V2__Seed_Data.sql
-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password, full_name, role, enabled)
VALUES ('admin', 'admin@wholesale.com', '$2a$10$cpVmqJT8T.EiZu9cgbNq9uaGwSiWpGdtaWYV8l/HgnxMCMBg/.PIC', 'System Administrator', 'ADMIN', true);

-- Insert sample manager (password: manager123)
INSERT INTO users (username, email, password, full_name, role, enabled)
VALUES ('manager', 'manager@wholesale.com', '$2a$10$cpVmqJT8T.EiZu9cgbNq9uaGwSiWpGdtaWYV8l/HgnxMCMBg/.PIC', 'John Manager', 'MANAGER', true);

-- Insert sample employee (password: employee123)
INSERT INTO users (username, email, password, full_name, role, enabled)
VALUES ('employee', 'employee@wholesale.com', '$2a$10$cpVmqJT8T.EiZu9cgbNq9uaGwSiWpGdtaWYV8l/HgnxMCMBg/.PIC', 'Jane Employee', 'EMPLOYEE', true);

-- Insert sample customers
INSERT INTO customers (full_name, phone, email, address, company_name, city, country, active)
VALUES 
('ABC Retail Store', '+1-555-0101', 'contact@abcretail.com', '123 Main St, Suite 100', 'ABC Retail Inc.', 'New York', 'USA', true),
('Fashion Forward LLC', '+1-555-0102', 'orders@fashionforward.com', '456 Fashion Ave', 'Fashion Forward LLC', 'Los Angeles', 'USA', true),
('Style Boutique', '+1-555-0103', 'buyer@styleboutique.com', '789 Style Blvd', 'Style Boutique Co.', 'Chicago', 'USA', true),
('Trendy Threads', '+1-555-0104', 'purchasing@trendythreads.com', '321 Trend Lane', 'Trendy Threads Inc.', 'Miami', 'USA', true),
('Classic Clothing Co.', '+1-555-0105', 'orders@classicclothing.com', '654 Classic Way', 'Classic Clothing Co.', 'Seattle', 'USA', true);

-- Insert sample products
INSERT INTO products (name, description, category, sku, price, size, color, active)
VALUES 
('Cotton T-Shirt Basic', 'High quality 100% cotton basic t-shirt', 'T-Shirts', 'TSH-001', 12.99, 'M', 'White', true),
('Cotton T-Shirt Basic', 'High quality 100% cotton basic t-shirt', 'T-Shirts', 'TSH-002', 12.99, 'L', 'Black', true),
('Denim Jeans Classic', 'Classic fit denim jeans', 'Jeans', 'JNS-001', 45.99, '32', 'Blue', true),
('Denim Jeans Slim', 'Slim fit denim jeans', 'Jeans', 'JNS-002', 49.99, '34', 'Dark Blue', true),
('Polo Shirt Premium', 'Premium cotton polo shirt', 'Polo Shirts', 'POL-001', 24.99, 'M', 'Navy', true),
('Polo Shirt Premium', 'Premium cotton polo shirt', 'Polo Shirts', 'POL-002', 24.99, 'L', 'Red', true),
('Hoodie Fleece', 'Warm fleece hoodie', 'Hoodies', 'HOD-001', 39.99, 'M', 'Gray', true),
('Hoodie Fleece', 'Warm fleece hoodie', 'Hoodies', 'HOD-002', 39.99, 'XL', 'Black', true),
('Dress Shirt Formal', 'Formal dress shirt for business wear', 'Dress Shirts', 'DRS-001', 34.99, 'M', 'White', true),
('Dress Shirt Formal', 'Formal dress shirt for business wear', 'Dress Shirts', 'DRS-002', 34.99, 'L', 'Light Blue', true);

-- Insert inventory for products
INSERT INTO inventory (product_id, quantity, min_quantity, max_quantity, warehouse_location, shelf_number)
VALUES 
(1, 500, 50, 1000, 'Warehouse A', 'A-01-01'),
(2, 450, 50, 1000, 'Warehouse A', 'A-01-02'),
(3, 200, 30, 500, 'Warehouse A', 'A-02-01'),
(4, 180, 30, 500, 'Warehouse A', 'A-02-02'),
(5, 300, 40, 600, 'Warehouse B', 'B-01-01'),
(6, 280, 40, 600, 'Warehouse B', 'B-01-02'),
(7, 150, 25, 400, 'Warehouse B', 'B-02-01'),
(8, 120, 25, 400, 'Warehouse B', 'B-02-02'),
(9, 250, 35, 500, 'Warehouse C', 'C-01-01'),
(10, 220, 35, 500, 'Warehouse C', 'C-01-02');

-- Insert sample orders
INSERT INTO orders (order_number, customer_id, status, total_amount, shipping_address, notes, order_date, created_by)
VALUES 
('ORD-2024-0001', 1, 'DELIVERED', 259.88, '123 Main St, Suite 100, New York, USA', 'Rush delivery requested', '2024-01-15 10:30:00', 1),
('ORD-2024-0002', 2, 'SHIPPED', 449.91, '456 Fashion Ave, Los Angeles, USA', NULL, '2024-01-20 14:45:00', 2),
('ORD-2024-0003', 3, 'PROCESSING', 179.94, '789 Style Blvd, Chicago, USA', 'Gift wrapping needed', '2024-01-25 09:15:00', 2),
('ORD-2024-0004', 4, 'PENDING', 324.90, '321 Trend Lane, Miami, USA', NULL, '2024-01-28 16:20:00', 3),
('ORD-2024-0005', 5, 'PENDING', 139.96, '654 Classic Way, Seattle, USA', 'Fragile items', '2024-01-30 11:00:00', 3);

-- Insert order items
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
VALUES 
(1, 1, 10, 12.99, 129.90),
(1, 2, 10, 12.99, 129.90),
(2, 3, 5, 45.99, 229.95),
(2, 4, 4, 49.99, 199.96),
(3, 5, 4, 24.99, 99.96),
(3, 7, 2, 39.99, 79.98),
(4, 9, 5, 34.99, 174.95),
(4, 10, 5, 34.99, 174.95),
(5, 1, 5, 12.99, 64.95),
(5, 5, 3, 24.99, 74.97);
