package com.fooddelivery.backend.config;

import com.fooddelivery.backend.entity.*;
import com.fooddelivery.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        long count = restaurantRepository.count();
        if (count >= 50) {
            System.out.println("Database already has " + count + " restaurants. Skipping seeding.");
            return;
        }

        System.out.println("Seeding database with 50 restaurants and 350+ dishes...");

        // Ensure roles, users, and payment methods are seeded first
        seedRolesAndUsers();

        // 1. Seed Categories (ensure 15 categories for diverse cuisines)
        Map<String, Category> categoryMap = seedCategories();

        // 2. Clear old restaurants (cascades to foods, reviews, etc. but let's clear explicitly to be safe)
        foodRepository.deleteAll();
        restaurantRepository.deleteAll();

        // Cuisines lists
        String[] cuisines = {
            "Pizza", "Burger", "South Indian", "North Indian", "Chinese",
            "Biryani", "BBQ", "Bakery", "Cafe", "Juice",
            "Seafood", "Healthy Food", "Ice Cream", "Desserts", "Street Food"
        };

        // Restaurant templates to generate 50 unique restaurants
        String[][] restaurantNames = {
            {"Pizza", "The Pizza Lab", "Pizza Heaven", "Slice of Life", "Dough & Cheese", "Crust Lovers"},
            {"Burger", "Burger Shack", "The Daily Grind", "Patty & Buns", "Gourmet Grill", "Slider Town"},
            {"South Indian", "Sagar Ratna", "Dakshin Express", "Madras Cafe", "Idli Street", "Dosa Junction"},
            {"North Indian", "Tandoori Nights", "Punjab Grill", "Dhaba 86", "Curry Leaf", "Masala Kraft"},
            {"Chinese", "Wok Star", "The Golden Dragon", "Dim Sum House", "Peking Diner", "Noodle Station"},
            {"Biryani", "Biryani House", "Royal Biryani", "Behrouz Feast", "Paradise Dum", "Deccan Spices"},
            {"BBQ", "Barbeque World", "Smoked Grill", "Sizzling Skewers", "Coal Pit", "The Roast Room"},
            {"Bakery", "Sweet Dreams", "The Daily Bread", "Crumb & Crust", "Bake House", "Pastry Palace"},
            {"Cafe", "The Coffee Club", "Brew Haven", "Mocha & More", "Chai Point", "Bean Street"},
            {"Juice", "The Juice Shop", "Squeeze & Sip", "Fruit Oasis", "Fresh & Healthy", "Nectar Bar"},
            {"Seafood", "Fisherman's Wharf", "Coastal Catch", "The Ocean Grill", "Crab Shack", "Sea Breeze"},
            {"Healthy Food", "Green Salad Co.", "The Fit Meal", "Nutri Bowl", "Lean Kitchen", "Organic Greens"},
            {"Ice Cream", "Scoops & Cones", "Gelato Italiano", "The Cold Stone", "Frosty Treats", "Ice Cream Parlor"},
            {"Desserts", "Waffle House", "The Chocolate Room", "Sugar Rush", "Donut Delight", "Sweet Treats"},
            {"Street Food", "Chaat Bazaar", "Mumbai Express", "The Golgappa Club", "Samosa Junction", "Kathi Roll Co."}
        };

        String[] addresses = {
            "12, MG Road, Bangalore", "34, Indiranagar, Bangalore", "56, Koramangala, Bangalore", "78, Jayanagar, Bangalore",
            "90, Malleshwaram, Bangalore", "11, HSR Layout, Bangalore", "45, Whitefield, Bangalore", "22, Sadashivanagar, Bangalore",
            "67, Banashankari, Bangalore", "89, Basavanagudi, Bangalore", "101, Outer Ring Road, Bangalore", "15, Cunningham Road, Bangalore"
        };

        String[] offers = {
            "50% OFF up to ₹100", "FREE Delivery on orders above ₹200", "Flat ₹50 OFF", "Buy 1 Get 1 Free", "20% OFF using UPI", "No Offer"
        };

        String[] covers = {
            "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=800&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1551024601-bec78aea704b?w=800&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=800&auto=format&fit=crop"
        };

        String[] logos = {
            "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=150&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=150&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=150&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=150&auto=format&fit=crop"
        };

        Random rand = new Random();

        // We generate exactly 50 restaurants
        int restaurantCount = 0;
        while (restaurantCount < 50) {
            // Pick a cuisine type index
            int typeIdx = restaurantCount % restaurantNames.length;
            String cuisineType = cuisines[typeIdx];
            
            // Generate name
            String[] namesForCuisine = restaurantNames[typeIdx];
            String baseName = namesForCuisine[rand.nextInt(namesForCuisine.length - 1) + 1];
            String name = baseName + " #" + (restaurantCount + 1);

            String desc = "Authentic " + cuisineType + " food, crafted with fresh ingredients and served with delight. Try our best sellers.";
            Double rating = 3.5 + rand.nextDouble() * 1.4; // between 3.5 and 4.9
            // round to 1 decimal place
            rating = Math.round(rating * 10.0) / 10.0;

            Integer deliveryTime = 15 + rand.nextInt(35); // 15 to 50 mins
            Double distance = 0.5 + rand.nextDouble() * 6.5; // 0.5 to 7.0 km
            distance = Math.round(distance * 10.0) / 10.0;

            Double minOrder = 99.0 + rand.nextInt(100);
            Integer costForTwo = 150 + rand.nextInt(650); // ₹150 to ₹800
            String offer = offers[rand.nextInt(offers.length)];
            
            String phone = "98765" + String.format("%05d", restaurantCount);

            Restaurant r = Restaurant.builder()
                .name(name)
                .description(desc)
                .cuisineType(cuisineType)
                .rating(rating)
                .deliveryTimeMins(deliveryTime)
                .distanceKm(distance)
                .deliveryCharge(distance * 10.0) // ₹10 per km
                .minOrderAmount(minOrder)
                .costForTwo(costForTwo)
                .offers(offer.equals("No Offer") ? null : offer)
                .logoUrl(logos[rand.nextInt(logos.length)])
                .address(addresses[rand.nextInt(addresses.length)])
                .phone(phone)
                .isOpened(rand.nextDouble() > 0.08) // 92% open
                .isActive(true)
                .images(new ArrayList<>())
                .build();

            // Cover Image
            RestaurantImage coverImage = RestaurantImage.builder()
                .restaurant(r)
                .imageUrl(covers[rand.nextInt(covers.length)])
                .isPrimary(true)
                .build();
            r.getImages().add(coverImage);

            Restaurant savedRestaurant = restaurantRepository.save(r);

            // Now seed foods for this restaurant (7 foods per restaurant = 350 foods total)
            seedFoodsForRestaurant(savedRestaurant, cuisineType, categoryMap, rand);

            restaurantCount++;
        }

        System.out.println("Seeding completed successfully! Total restaurants: 50. Total dishes: " + foodRepository.count());
    }

    private void seedRolesAndUsers() {
        // Ensure default roles exist
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
            .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_CUSTOMER").build()));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));

        // Ensure users exist
        if (!userRepository.findByEmail("admin@fooddelivery.com").isPresent()) {
            User admin = User.builder()
                .name("System Admin")
                .email("admin@fooddelivery.com")
                .password("$2a$12$R9h/lIPzNgb.aQ1YnGPpKeWOBW7812t3pGfe33dF2Lq51u9dK72.e") // BCrypt password
                .phone("9876543210")
                .isActive(true)
                .roles(new HashSet<>(Arrays.asList(adminRole)))
                .build();
            userRepository.save(admin);
        }

        if (!userRepository.findByEmail("john@gmail.com").isPresent()) {
            User customer = User.builder()
                .name("John Doe")
                .email("john@gmail.com")
                .password("$2a$12$R9h/lIPzNgb.aQ1YnGPpKeWOBW7812t3pGfe33dF2Lq51u9dK72.e") // BCrypt password
                .phone("9876543211")
                .isActive(true)
                .roles(new HashSet<>(Arrays.asList(customerRole)))
                .build();
            userRepository.save(customer);
        }

        // Ensure payment methods exist
        String[] methods = {"UPI", "CREDIT_CARD", "DEBIT_CARD", "NET_BANKING", "WALLET", "CASH_ON_DELIVERY"};
        for (int i = 0; i < methods.length; i++) {
            final String mName = methods[i];
            if (!paymentMethodRepository.findByName(mName).isPresent()) {
                paymentMethodRepository.save(PaymentMethod.builder().id((long)(i+1)).name(mName).isActive(true).build());
            }
        }

        // Ensure coupons exist
        if (!couponRepository.findByCode("FIRST50").isPresent()) {
            couponRepository.save(Coupon.builder()
                .code("FIRST50")
                .discountPercentage(50)
                .discountAmount(0.0)
                .maxDiscountAmount(100.0)
                .minOrderValue(150.0)
                .expiryDate(java.time.LocalDateTime.now().plusYears(1))
                .isActive(true)
                .build());
        }
    }

    private Map<String, Category> seedCategories() {
        String[][] categoriesInfo = {
            {"Biryani", "Aromatic basmati rice layered with spiced meats or vegetables", "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=300&auto=format&fit=crop"},
            {"Pizza", "Cheesy Italian crusts with delicious toppings", "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=300&auto=format&fit=crop"},
            {"Burger", "Juicy grilled patties inside soft toasted buns", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300&auto=format&fit=crop"},
            {"Chinese", "Stir-fried noodles, spring rolls, and soups", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=300&auto=format&fit=crop"},
            {"South Indian", "Light and healthy dosas, idlis, and wadas", "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=300&auto=format&fit=crop"},
            {"Indian", "Rich and traditional North and South Indian curries", "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=300&auto=format&fit=crop"},
            {"Dessert", "Sweet pastries, cakes, waffles, and ice cream", "https://images.unsplash.com/photo-1551024601-bec78aea704b?w=300&auto=format&fit=crop"},
            {"Drinks", "Fresh juices, mocktails, milkshakes, and hot beverages", "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=300&auto=format&fit=crop"}
        };

        Map<String, Category> map = new HashMap<>();
        for (String[] cat : categoriesInfo) {
            Category category = categoryRepository.findByName(cat[0])
                .orElseGet(() -> categoryRepository.save(Category.builder()
                    .name(cat[0])
                    .description(cat[1])
                    .imageUrl(cat[2])
                    .build()));
            map.put(cat[0], category);
        }
        return map;
    }

    private void seedFoodsForRestaurant(Restaurant r, String cuisineType, Map<String, Category> categoryMap, Random rand) {
        // We define food mock templates matching each cuisine
        String[][] foodTemplates;
        switch (cuisineType) {
            case "Pizza":
                foodTemplates = new String[][]{
                    {"Margherita Pizza", "Classic cheese and tomato pizza", "249", "Pizza", "true", "mozzarella, fresh basil, olive oil, tomato sauce", "280"},
                    {"Farmhouse Pizza", "Garden fresh capsicum, onion, mushroom, tomato", "349", "Pizza", "true", "capsicum, red onions, mushrooms, diced tomatoes, mozzarella", "320"},
                    {"Paneer Tikka Pizza", "Spiced paneer cubes with onions and peppers", "379", "Pizza", "true", "tandoori paneer, capsicum, red onions, green chillies, mozzarella", "360"},
                    {"Pepperoni Feast", "Loads of pork pepperoni and stringy mozzarella", "449", "Pizza", "false", "sliced pepperoni, mozzarella, parmesan, oregano", "450"},
                    {"Garlic Breadsticks", "Freshly baked garlic butter breadsticks", "129", "Pizza", "true", "wheat flour, garlic butter, herbs, mozzarella", "180"},
                    {"Stuffed Garlic Bread", "Garlic bread loaded with sweet corn and cheese", "159", "Pizza", "true", "garlic bread, sweet corn, jalapenos, cheese dip", "220"},
                    {"Choco Lava Cake", "Hot chocolate cake with a gooey liquid center", "99", "Dessert", "true", "dark chocolate, cocoa powder, butter, sugar", "350"}
                };
                break;
            case "Burger":
                foodTemplates = new String[][]{
                    {"Classic Cheese Burger", "Grilled chicken patty with melting cheese slice", "189", "Burger", "false", "chicken patty, cheddar cheese, lettuce, burger sauce, bun", "400"},
                    {"Crispy Veg Burger", "Spiced potato patty with crisp lettuce and mayo", "129", "Burger", "true", "crispy potato patty, garlic mayo, tomato, onion, lettuce", "310"},
                    {"BBQ Chicken Burger", "Chicken patty dipped in smoky BBQ sauce and onions", "209", "Burger", "false", "grilled chicken, hickory BBQ sauce, onion rings, cheddar", "420"},
                    {"Double Cheese Burger", "Two flame-grilled beef/mutton patties with double cheese", "299", "Burger", "false", "double mutton patties, double cheddar, pickles, mustard, bun", "580"},
                    {"French Fries Large", "Crispy golden salted potato fries", "119", "Burger", "true", "russet potatoes, iodized salt, vegetable oil", "240"},
                    {"Onion Rings", "Batter-fried crispy onion rings served with dip", "99", "Burger", "true", "sliced onions, flour batter, breadcrumbs, cajun spice", "180"},
                    {"Oreo Milkshake", "Creamy vanilla ice cream blended with Oreo cookies", "129", "Drinks", "true", "whole milk, vanilla ice cream, crushed oreos", "380"}
                };
                break;
            case "Biryani":
                foodTemplates = new String[][]{
                    {"Chicken Dum Biryani", "Aromatic long grain basmati rice with spiced chicken", "299", "Biryani", "false", "basmati rice, marinated chicken, saffron, mint, yoghurt", "520"},
                    {"Mutton Biryani", "Royal Hyderabadi mutton dum biryani served with raita", "379", "Biryani", "false", "goat meat pieces, basmati rice, biryani spices, ghee", "580"},
                    {"Paneer Tikka Biryani", "Fragrant rice with roasted paneer cubes and tikka gravy", "259", "Biryani", "true", "basmati rice, paneer tikka, fried onions, cardamoms", "460"},
                    {"Egg Biryani", "Fragrant spiced rice layered with boiled eggs", "219", "Biryani", "false", "basmati rice, hard-boiled eggs, biryani gravy, saffron", "390"},
                    {"Chicken Kabab (6 Pcs)", "Spicy deep-fried chicken starters with bone", "199", "Biryani", "false", "chicken pieces, red chilli paste, ginger-garlic, curry leaves", "310"},
                    {"Raita", "Cooling spiced yoghurt dip with cucumber and onion", "49", "Indian", "true", "yoghurt, cucumber, roasted cumin powder, coriander", "60"},
                    {"Filter Coffee", "Frothy traditional South Indian chicory blend coffee", "39", "Drinks", "true", "milk, dark roasted coffee decoction, sugar", "90"}
                };
                break;
            case "South Indian":
                foodTemplates = new String[][]{
                    {"Masala Dosa", "Crispy rice crepe with spiced potato filling", "99", "South Indian", "true", "rice batter, potato masala, ghee, coconut chutney, sambar", "210"},
                    {"Idli Vada Combo", "Two soft steamed idlis and one crispy medu vada", "79", "South Indian", "true", "fermented rice-urad dal batter, sambar, tomato chutney", "160"},
                    {"Rava Masala Dosa", "Crispy semolina crepe with onion and potato bhaji", "119", "South Indian", "true", "semolina, rice flour, potato masala, black pepper, ginger", "230"},
                    {"Onion Uttapam", "Thick savory pancake topped with finely chopped onions", "99", "South Indian", "true", "rice batter, onions, green chillies, coriander, ghee", "220"},
                    {"Medu Vada (2 Pcs)", "Crispy deep-fried lentil donuts served with sambar", "69", "South Indian", "true", "urad dal paste, green chillies, black pepper, curry leaves", "170"},
                    {"Kesari Bath", "Sweet semolina dessert roasted in pure ghee and cashews", "69", "Dessert", "true", "semolina, ghee, sugar, cashews, raisins, saffron", "290"},
                    {"Badam Milk", "Warm milk enriched with saffron and crushed almonds", "59", "Drinks", "true", "almonds, milk, saffron, cardamom, sugar", "140"}
                };
                break;
            case "Chinese":
                foodTemplates = new String[][]{
                    {"Veg Hakka Noodles", "Stir-fried noodles with crunchy spring vegetables", "179", "Chinese", "true", "noodles, cabbage, carrots, spring onions, soy sauce, garlic", "260"},
                    {"Chicken Fried Rice", "Wok-tossed rice with egg, chicken, and soy sauce", "219", "Chinese", "false", "basmati rice, chicken strips, egg, green peas, spring onion", "340"},
                    {"Veg Manchurian Dry", "Fried mixed veg balls in tangy and spicy sauce", "189", "Chinese", "true", "cabbage, carrot, cornflour, ginger, green chillies, soy sauce", "230"},
                    {"Chilli Chicken Garlic", "Batter-fried chicken tossed in hot garlic chilli sauce", "249", "Chinese", "false", "boneless chicken, bell peppers, soy sauce, garlic, green chillies", "320"},
                    {"Spring Rolls Veg", "Crispy golden rolls filled with sautéed veggies", "119", "Chinese", "true", "pastry sheets, cabbage, carrot, sprouts, sweet chilli dip", "150"},
                    {"Manchow Soup Veg", "Spicy hot soup with crispy fried noodles", "99", "Chinese", "true", "mixed vegetables, ginger, garlic, vinegar, fried noodles", "110"},
                    {"Sweet Corn Soup Chicken", "Creamy chicken soup with crushed sweet corn", "119", "Chinese", "false", "chicken broth, sweet corn, egg drops, white pepper", "130"}
                };
                break;
            default: // Default North Indian templates
                foodTemplates = new String[][]{
                    {"Paneer Butter Masala", "Cottage cheese in rich tomato cream gravy", "249", "Indian", "true", "paneer cubes, butter, cashew paste, fresh cream, tomato puree", "380"},
                    {"Butter Chicken", "Grilled chicken tikka in sweet and spicy gravy", "289", "Indian", "false", "tandoori chicken, butter, cream, honey, fenugreek leaves", "440"},
                    {"Dal Makhani", "Slow-cooked black lentils with butter and cream", "199", "Indian", "true", "black urad dal, rajma, ghee, butter, fresh cream, tomato paste", "290"},
                    {"Tandoori Roti (Plain)", "Whole wheat tandoor-baked flatbread", "25", "Indian", "true", "whole wheat flour, water, salt", "90"},
                    {"Butter Naan", "Tear-shaped leavened flatbread brushed with butter", "49", "Indian", "true", "all-purpose flour, yeast, butter, milk, yoghurt", "180"},
                    {"Jeera Rice", "Basmati rice tempered with ghee and cumin seeds", "129", "Indian", "true", "basmati rice, ghee, cumin seeds, bay leaf", "190"},
                    {"Gulab Jamun (2 Pcs)", "Soft milk-solid balls in sweet cardamom syrup", "69", "Dessert", "true", "khoya, chenna, sugar syrup, green cardamom, rose water", "220"}
                };
                break;
        }

        String[] foodUrls = {
            "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1593560708920-61dd98c46a4e?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1544982503-9f984c14501a?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1550547660-d9450f859349?w=500&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500&auto=format&fit=crop"
        };

        for (int i = 0; i < foodTemplates.length; i++) {
            String[] fTmp = foodTemplates[i];
            
            // Map category name to Category object
            String catName = fTmp[3];
            Category catObj = categoryMap.get(catName);
            if (catObj == null) {
                // fallback to Pizza or Dessert if category not found
                catObj = categoryMap.values().iterator().next();
            }

            Double price = Double.parseDouble(fTmp[2]);
            Boolean isVeg = Boolean.parseBoolean(fTmp[4]);
            Integer calories = Integer.parseInt(fTmp[6]);
            
            Double rating = 3.8 + rand.nextDouble() * 1.1; // between 3.8 and 4.9
            rating = Math.round(rating * 10.0) / 10.0;

            Food food = Food.builder()
                .restaurant(r)
                .category(catObj)
                .name(fTmp[0])
                .description(fTmp[1])
                .price(price)
                .isVeg(isVeg)
                .ingredients(fTmp[5])
                .calories(calories)
                .preparationTime(10 + rand.nextInt(20)) // 10 to 30 mins
                .rating(rating)
                .isAvailable(true)
                .images(new ArrayList<>())
                .build();

            FoodImage img = FoodImage.builder()
                .food(food)
                .imageUrl(foodUrls[i % foodUrls.length])
                .isPrimary(true)
                .build();
            food.getImages().add(img);

            foodRepository.save(food);
        }
    }
}
