package com.fooddelivery.backend.service.impl;

import com.fooddelivery.backend.entity.Restaurant;
import com.fooddelivery.backend.entity.Food;
import com.fooddelivery.backend.entity.Category;
import com.fooddelivery.backend.exception.ResourceNotFoundException;
import com.fooddelivery.backend.repository.RestaurantRepository;
import com.fooddelivery.backend.service.RestaurantService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Restaurant> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findByIsActiveTrue(pageable);
    }

    @Override
    public Page<Restaurant> getFilteredRestaurants(
            String search, Long categoryId, String cuisine, Boolean vegOnly,
            Double minRating, Integer maxDeliveryTime, String priceRange,
            String sort, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // --- Count query ---
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Restaurant> countRoot = countQuery.from(Restaurant.class);
        countQuery.select(cb.countDistinct(countRoot));
        List<Predicate> countPredicates = buildPredicates(cb, countQuery, countRoot,
                search, categoryId, cuisine, vegOnly, minRating, maxDeliveryTime, priceRange);
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        // --- Data query ---
        CriteriaQuery<Restaurant> dataQuery = cb.createQuery(Restaurant.class);
        Root<Restaurant> root = dataQuery.from(Restaurant.class);
        dataQuery.select(root).distinct(true);
        List<Predicate> predicates = buildPredicates(cb, dataQuery, root,
                search, categoryId, cuisine, vegOnly, minRating, maxDeliveryTime, priceRange);
        dataQuery.where(predicates.toArray(new Predicate[0]));

        // Sorting
        if (sort != null) {
            switch (sort) {
                case "rating" -> dataQuery.orderBy(cb.desc(root.get("rating")));
                case "deliveryTime" -> dataQuery.orderBy(cb.asc(root.get("deliveryTimeMins")));
                case "costLowToHigh" -> dataQuery.orderBy(cb.asc(root.get("costForTwo")));
                case "costHighToLow" -> dataQuery.orderBy(cb.desc(root.get("costForTwo")));
                default -> dataQuery.orderBy(cb.desc(root.get("rating")));
            }
        } else {
            dataQuery.orderBy(cb.desc(root.get("rating")));
        }

        TypedQuery<Restaurant> typedQuery = entityManager.createQuery(dataQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Restaurant> results = typedQuery.getResultList();
        return new PageImpl<>(results, pageable, total);
    }

    private <T> List<Predicate> buildPredicates(
            CriteriaBuilder cb, CriteriaQuery<T> query, Root<Restaurant> root,
            String search, Long categoryId, String cuisine, Boolean vegOnly,
            Double minRating, Integer maxDeliveryTime, String priceRange) {

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isTrue(root.get("isActive")));

        // Text search across restaurant name, cuisine, description, food name, and category name
        if (search != null && !search.trim().isEmpty()) {
            String pattern = "%" + search.trim().toLowerCase() + "%";

            // Subquery: find restaurant IDs where food name or food category name matches
            Subquery<Long> foodSearchSub = query.subquery(Long.class);
            Root<Food> foodSearchRoot = foodSearchSub.from(Food.class);
            Join<Food, Category> foodCatJoin = foodSearchRoot.join("category", JoinType.LEFT);
            foodSearchSub.select(foodSearchRoot.get("restaurant").get("id"));
            foodSearchSub.where(cb.or(
                cb.like(cb.lower(foodSearchRoot.get("name")), pattern),
                cb.like(cb.lower(foodCatJoin.get("name")), pattern)
            ));

            predicates.add(cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("cuisineType")), pattern),
                cb.like(cb.lower(root.get("description")), pattern),
                root.get("id").in(foodSearchSub)
            ));
        }

        // Category filter: restaurants that have at least one food in this category
        if (categoryId != null) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Food> foodSub = subquery.from(Food.class);
            subquery.select(foodSub.get("restaurant").get("id"));
            subquery.where(cb.equal(foodSub.get("category").get("id"), categoryId));
            predicates.add(root.get("id").in(subquery));
        }

        // Cuisine filter
        if (cuisine != null && !cuisine.trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("cuisineType")), cuisine.trim().toLowerCase()));
        }

        // Veg-only filter: restaurants with at least one veg food
        if (vegOnly != null && vegOnly) {
            Subquery<Long> vegSub = query.subquery(Long.class);
            Root<Food> vegFood = vegSub.from(Food.class);
            vegSub.select(vegFood.get("restaurant").get("id"));
            vegSub.where(cb.isTrue(vegFood.get("isVeg")));
            predicates.add(root.get("id").in(vegSub));
        }

        // Minimum rating filter
        if (minRating != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), minRating));
        }

        // Max delivery time filter
        if (maxDeliveryTime != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("deliveryTimeMins"), maxDeliveryTime));
        }

        // Price range filter on costForTwo
        if (priceRange != null && !priceRange.isEmpty()) {
            switch (priceRange) {
                case "under200" -> predicates.add(cb.lessThan(root.get("costForTwo"), 200));
                case "200to500" -> {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("costForTwo"), 200));
                    predicates.add(cb.lessThanOrEqualTo(root.get("costForTwo"), 500));
                }
                case "above500" -> predicates.add(cb.greaterThan(root.get("costForTwo"), 500));
            }
        }

        return predicates;
    }

    @Override
    public Page<Restaurant> searchRestaurants(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return getAllRestaurants(pageable);
        }
        return restaurantRepository.searchRestaurants(query.trim(), pageable);
    }

    @Override
    public Page<Restaurant> getRestaurantsByCuisine(String cuisine, Pageable pageable) {
        return restaurantRepository.findByCuisineTypeAndIsActiveTrue(cuisine, pageable);
    }

    @Override
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + id));
    }

    @Override
    @Transactional
    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public Restaurant updateRestaurant(Long id, Restaurant restaurantDetails) {
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setName(restaurantDetails.getName());
        restaurant.setDescription(restaurantDetails.getDescription());
        restaurant.setCuisineType(restaurantDetails.getCuisineType());
        restaurant.setDeliveryTimeMins(restaurantDetails.getDeliveryTimeMins());
        restaurant.setDistanceKm(restaurantDetails.getDistanceKm());
        restaurant.setDeliveryCharge(restaurantDetails.getDeliveryCharge());
        restaurant.setMinOrderAmount(restaurantDetails.getMinOrderAmount());
        restaurant.setAddress(restaurantDetails.getAddress());
        restaurant.setPhone(restaurantDetails.getPhone());
        restaurant.setIsActive(restaurantDetails.getIsActive());
        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurantRepository.delete(restaurant);
    }
}
