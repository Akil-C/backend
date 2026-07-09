USE food_delivery;

-- Seed Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_CUSTOMER') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN') ON DUPLICATE KEY UPDATE name=name;

-- Seed Users (Password is 'password' BCrypt hashed)
-- Admin
INSERT INTO users (id, name, email, password, phone, is_active)
VALUES (1, 'System Admin', 'admin@fooddelivery.com', '$2a$12$R9h/lIPzNgb.aQ1YnGPpKeWOBW7812t3pGfe33dF2Lq51u9dK72.e', '9876543210', TRUE)
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO user_roles (user_id, role_id) VALUES (1, 2) ON DUPLICATE KEY UPDATE role_id=role_id;

-- Customer
INSERT INTO users (id, name, email, password, phone, is_active)
VALUES (2, 'John Doe', 'john@gmail.com', '$2a$12$R9h/lIPzNgb.aQ1YnGPpKeWOBW7812t3pGfe33dF2Lq51u9dK72.e', '9876543211', TRUE)
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO user_roles (user_id, role_id) VALUES (2, 1) ON DUPLICATE KEY UPDATE role_id=role_id;

-- Seed Payment Methods
INSERT INTO payment_methods (id, name, is_active) VALUES (1, 'UPI', TRUE) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO payment_methods (id, name, is_active) VALUES (2, 'CREDIT_CARD', TRUE) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO payment_methods (id, name, is_active) VALUES (3, 'DEBIT_CARD', TRUE) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO payment_methods (id, name, is_active) VALUES (4, 'NET_BANKING', TRUE) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO payment_methods (id, name, is_active) VALUES (5, 'WALLET', TRUE) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO payment_methods (id, name, is_active) VALUES (6, 'CASH_ON_DELIVERY', TRUE) ON DUPLICATE KEY UPDATE name=name;

-- Seed Delivery Partners
INSERT INTO delivery_partners (id, name, phone, vehicle_number, status, current_lat, current_lng) VALUES
(1, 'Ramesh Kumar', '9876543220', 'KA-01-EF-1234', 'AVAILABLE', 12.9716, 77.5946),
(2, 'Suresh Singh', '9876543221', 'KA-03-GH-5678', 'AVAILABLE', 12.9720, 77.5950),
(3, 'Amit Patel', '9876543222', 'KA-05-IJ-9012', 'AVAILABLE', 12.9710, 77.5940)
ON DUPLICATE KEY UPDATE name=name;

-- Seed Categories
INSERT INTO categories (id, name, description, image_url) VALUES
(1, 'Biryani', 'Richly flavored aromatic rice dishes with spices', 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500&auto=format&fit=crop'),
(2, 'Pizza', 'Freshly baked pizzas with cheesy toppings', 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500&auto=format&fit=crop'),
(3, 'Burger', 'Juicy burgers with crispy patties and sauces', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&auto=format&fit=crop'),
(4, 'Chinese', 'Noodles, fried rice, and savory starters', 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500&auto=format&fit=crop'),
(5, 'South Indian', 'Idlis, dosas, vadas and filter coffee', 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=500&auto=format&fit=crop'),
(6, 'Desserts', 'Sweet treats, ice creams, and pastries', 'https://images.unsplash.com/photo-1551024601-bec78aea704b?w=500&auto=format&fit=crop')
ON DUPLICATE KEY UPDATE name=name;

-- Seed Restaurants
INSERT INTO restaurants (id, name, description, cuisine_type, rating, delivery_time_mins, distance_km, delivery_charge, min_order_amount, address, phone, is_active) VALUES
(1, 'The Pizza Lab', 'Pizzas, Italian, Fast Food', 'Pizza', 4.6, 35, 2.4, 25.00, 150.00, '12, MG Road, Bangalore', '080-1234567', TRUE),
(2, 'Burger Shack', 'Gourmet burgers, milkshakes, fries', 'Burger', 4.4, 25, 1.9, 20.00, 100.00, '34, Indiranagar, Bangalore', '080-2345678', TRUE),
(3, 'Wok Star', 'Pan-Asian, Chinese, Thai', 'Chinese', 4.5, 40, 3.2, 35.00, 200.00, '56, Koramangala, Bangalore', '080-3456789', TRUE),
(4, 'Biryani House', 'Authentic Hyderabadi and Lucknowi Biryani', 'Biryani', 4.6, 30, 2.8, 30.00, 150.00, '78, Jayanagar, Bangalore', '080-4567890', TRUE),
(5, 'Sagar Ratna', 'Pure veg South Indian delicacies', 'South Indian', 4.3, 20, 1.5, 15.00, 100.00, '90, Malleshwaram, Bangalore', '080-5678901', TRUE),
(6, 'Sweet Dreams', 'Pastries, waffles, ice creams', 'Desserts', 4.5, 20, 1.2, 15.00, 50.00, '11, HSR Layout, Bangalore', '080-6789012', TRUE)
ON DUPLICATE KEY UPDATE name=name;

-- Seed Restaurant Images
INSERT INTO restaurant_images (id, restaurant_id, image_url, is_primary) VALUES
(1, 1, 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500&auto=format&fit=crop', TRUE),
(2, 2, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&auto=format&fit=crop', TRUE),
(3, 3, 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500&auto=format&fit=crop', TRUE),
(4, 4, 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500&auto=format&fit=crop', TRUE),
(5, 5, 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=500&auto=format&fit=crop', TRUE),
(6, 6, 'https://images.unsplash.com/photo-1551024601-bec78aea704b?w=500&auto=format&fit=crop', TRUE)
ON DUPLICATE KEY UPDATE restaurant_id=restaurant_id;

-- Seed Foods
INSERT INTO foods (id, restaurant_id, category_id, name, description, price, is_veg, is_available, rating) VALUES
-- The Pizza Lab Foods
(1, 1, 2, 'Margherita Pizza', 'Classic delight with 100% real mozzarella cheese', 249.00, TRUE, TRUE, 4.7),
(2, 1, 2, 'Farmhouse Pizza', 'Loaded with fresh veggies: onion, capsicum, tomato, mushroom', 349.00, TRUE, TRUE, 4.6),
(3, 1, 2, 'Pepperoni Feast', 'Double pepperonis, mozzarella cheese, Italian seasoning', 449.00, FALSE, TRUE, 4.8),
(4, 1, 2, 'Garlic Breadsticks', 'Baked to golden brown, served with cheesy dip', 129.00, TRUE, TRUE, 4.4),

-- Burger Shack Foods
(5, 2, 3, 'Classic Cheese Burger', 'Flame-grilled beef patty, cheddar, lettuce, tomato, special sauce', 189.00, FALSE, TRUE, 4.5),
(6, 2, 3, 'Crispy Veg Burger', 'Potato and peas patty, mayo, lettuce, toasted buns', 129.00, TRUE, TRUE, 4.3),
(7, 2, 3, 'BBQ Chicken Burger', 'Crispy chicken patty dipped in smoked BBQ sauce, onion rings', 209.00, FALSE, TRUE, 4.6),

-- Wok Star Foods
(8, 3, 4, 'Veg Hakka Noodles', 'Stir-fried noodles with crisp vegetables and soy sauce', 179.00, TRUE, TRUE, 4.4),
(9, 3, 4, 'Chicken Fried Rice', 'Stir-fried rice with eggs, chicken bits, and green onions', 219.00, FALSE, TRUE, 4.5),
(10, 3, 4, 'Spring Rolls', 'Crispy pastry sheets loaded with vegetables, deep fried', 119.00, TRUE, TRUE, 4.2),

-- Biryani House Foods
(11, 4, 1, 'Hyderabadi Chicken Biryani', 'Aromatic basmati rice layered with spiced chicken, served with raita', 299.00, FALSE, TRUE, 4.8),
(12, 4, 1, 'Paneer Tikka Biryani', 'Fragrant rice with roasted paneer cubes and tikka masala', 259.00, TRUE, TRUE, 4.4),
(13, 4, 1, 'Egg Biryani', 'Rice layered with boiled eggs and aromatic spices', 219.00, FALSE, TRUE, 4.3),

-- Sagar Ratna Foods
(14, 5, 5, 'Masala Dosa', 'Crispy rice crepe filled with potato masala, served with chutneys and sambar', 99.00, TRUE, TRUE, 4.6),
(15, 5, 5, 'Idli Vada Combo', '2 steamed idlis and 1 crispy medu vada', 79.00, TRUE, TRUE, 4.5),
(16, 5, 5, 'Filter Coffee', 'Traditional South Indian frothy filter coffee', 49.00, TRUE, TRUE, 4.7),

-- Sweet Dreams Foods
(17, 6, 6, 'Chocolate Fudge Brownie', 'Rich, warm chocolate brownie topped with chocolate syrup', 99.00, TRUE, TRUE, 4.7),
(18, 6, 6, 'Vanilla Ice Cream Scoop', 'Classic creamy vanilla bean ice cream', 59.00, TRUE, TRUE, 4.4),
(19, 6, 6, 'Red Velvet Pastry', 'Layered sponge cake with cream cheese frosting', 119.00, TRUE, TRUE, 4.6)
ON DUPLICATE KEY UPDATE name=name;

-- Seed Food Images
INSERT INTO food_images (id, food_id, image_url, is_primary) VALUES
(1, 1, 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=500&auto=format&fit=crop', TRUE),
(2, 2, 'https://images.unsplash.com/photo-1593560708920-61dd98c46a4e?w=500&auto=format&fit=crop', TRUE),
(3, 3, 'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=500&auto=format&fit=crop', TRUE),
(4, 4, 'https://images.unsplash.com/photo-1544982503-9f984c14501a?w=500&auto=format&fit=crop', TRUE),
(5, 5, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&auto=format&fit=crop', TRUE),
(6, 6, 'https://images.unsplash.com/photo-1550547660-d9450f859349?w=500&auto=format&fit=crop', TRUE),
(7, 7, 'https://images.unsplash.com/photo-1610440042657-612c34d95e9f?w=500&auto=format&fit=crop', TRUE),
(8, 8, 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500&auto=format&fit=crop', TRUE),
(9, 9, 'https://images.unsplash.com/photo-1603133872878-685f208b849a?w=500&auto=format&fit=crop', TRUE),
(10, 10, 'https://images.unsplash.com/photo-1544025162-d76694265947?w=500&auto=format&fit=crop', TRUE),
(11, 11, 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500&auto=format&fit=crop', TRUE),
(12, 12, 'https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=500&auto=format&fit=crop', TRUE),
(13, 13, 'https://images.unsplash.com/photo-1642821373181-696a54913e93?w=500&auto=format&fit=crop', TRUE),
(14, 14, 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=500&auto=format&fit=crop', TRUE),
(15, 15, 'https://images.unsplash.com/photo-1601050690597-df056fb4ce78?w=500&auto=format&fit=crop', TRUE),
(16, 16, 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500&auto=format&fit=crop', TRUE),
(17, 17, 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&auto=format&fit=crop', TRUE),
(18, 18, 'https://images.unsplash.com/photo-1570197788417-0e5f548c515a?w=500&auto=format&fit=crop', TRUE),
(19, 19, 'https://images.unsplash.com/photo-1586985289688-ca9cf49d350e?w=500&auto=format&fit=crop', TRUE)
ON DUPLICATE KEY UPDATE food_id=food_id;

-- Seed Coupons
INSERT INTO coupons (id, code, discount_percentage, discount_amount, max_discount_amount, min_order_value, expiry_date, is_active) VALUES
(1, 'WELCOME50', 50, 0.00, 100.00, 150.00, '2027-12-31 23:59:59', TRUE),
(2, 'FREEPAY', 10, 0.00, 50.00, 100.00, '2027-12-31 23:59:59', TRUE),
(3, 'FLAT100', 0, 100.00, 100.00, 400.00, '2027-12-31 23:59:59', TRUE)
ON DUPLICATE KEY UPDATE code=code;

-- Seed Settings
INSERT INTO settings (id, config_key, config_value) VALUES
(1, 'tax_rate_percentage', '5.0'),
(2, 'platform_fee', '2.00'),
(3, 'delivery_charge_per_km', '10.00')
ON DUPLICATE KEY UPDATE config_value=config_value;
