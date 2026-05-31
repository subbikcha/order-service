package com.ripple.order_service.service;

import com.ripple.order_service.dto.CreateOrderRequest;
import com.ripple.order_service.dto.RecommendationDTO;
import com.ripple.order_service.dto.UpdateOrderStatusRequest;
import com.ripple.order_service.dto.UserDTO;
import com.ripple.order_service.entity.Order;
import com.ripple.order_service.entity.OrderItem;
import com.ripple.order_service.enums.OrderStatus;
import com.ripple.order_service.exception.OrderNotFoundException;
import com.ripple.order_service.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order createOrder(String userId, CreateOrderRequest req) {

        // ── 1. Fetch user — use userId, userName, tier, rewardPoints,
        //       email, phoneNumber, address, walletBalance, isActive ──────────
        UserDTO user = restTemplate.getForObject(
                "http://localhost:8081/users/" + userId,
                UserDTO.class
        );

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new IllegalStateException("User account is inactive and cannot place orders.");
        }

        // ── 2. Build order items ───────────────────────────────────────────────
        List<OrderItem> items = req.getItems() != null
                ? req.getItems().stream().map(i -> OrderItem.builder()
                        .itemName(i.getItemName())
                        .category(i.getCategory())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .build()).toList()
                : List.of();

        int itemsTotal = items.stream()
                .mapToInt(i -> (int) (i.getUnitPrice() * i.getQuantity()))
                .sum();
        if (itemsTotal == 0) itemsTotal = 500;

        // ── 3. Delivery fee: premium tier gets free delivery ──────────────────
        int deliveryFee = "premium".equalsIgnoreCase(user.getTier()) ? 0 : 50;

        // ── 4. Reward-points discount: 100+ pts → ₹50 off ────────────────────
        int discount = 0;
        if (user.getRewardPoints() != null && user.getRewardPoints() >= 1000) {
            discount = 50;
        }

        // ── 5. Wallet discount: walletBalance ≥ 200 → extra ₹100 off ─────────
        int walletDiscount = 0;
        if (user.getWalletBalance() != null && user.getWalletBalance() >= 200.0) {
            walletDiscount = 100;
        }

        int finalAmount = itemsTotal - discount - walletDiscount + deliveryFee;

        // ── 6. Default delivery address from user-service if not supplied ─────
        String deliveryAddress = (req.getDeliveryAddress() != null && !req.getDeliveryAddress().isBlank())
                ? req.getDeliveryAddress()
                : user.getAddress();

        // ── 7. Call recommendation-service to get user's latest recommendation ─
        String recommendedHint = null;
        boolean matchedRec = false;
        try {
            RecommendationDTO[] recs = restTemplate.getForObject(
                    "http://localhost:8083/recommendations/user/" + userId,
                    RecommendationDTO[].class
            );
            if (recs != null && recs.length > 0) {
                recommendedHint = recs[0].getRestaurant();
                String orderedRestaurant = req.getRestaurantName();
                if (orderedRestaurant != null && orderedRestaurant.equalsIgnoreCase(recommendedHint)) {
                    matchedRec = true;
                    // Bonus: 10 extra reward points logged (informational — actual update via user-service)
                }
            }
        } catch (Exception ignored) {
            // recommendation-service unavailable — proceed without hint
        }

        Order order = Order.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .tier(user.getTier())
                .totalAmount(itemsTotal)
                .deliveryFee(deliveryFee)
                .discountAmount(discount)
                .walletDiscount(walletDiscount)
                .finalAmount(finalAmount)
                .restaurantName(req.getRestaurantName() != null ? req.getRestaurantName() : "QuickBite Kitchen")
                .deliveryAddress(deliveryAddress)
                .notes(req.getNotes())
                .status(OrderStatus.PENDING)
                .customerEmail(user.getEmail())
                .contactPhone(user.getPhoneNumber())
                .recommendedRestaurantHint(recommendedHint)
                .matchedRecommendation(matchedRec)
                .items(items)
                .build();

        return repository.save(order);
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public Order getOrderById(String orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public List<Order> getOrdersByUser(String userId) {
        return repository.findByUserId(userId);
    }

    public Order updateOrderStatus(String orderId, UpdateOrderStatusRequest req) {
        Order order = getOrderById(orderId);
        order.setStatus(req.getStatus());
        return repository.save(order);
    }

    public Order cancelOrder(String orderId) {
        Order order = getOrderById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        return repository.save(order);
    }

    public Map<String, Object> getOrderStats() {
        List<Order> all = repository.findAll();
        long delivered  = all.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        long pending    = all.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long cancelled  = all.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();
        long matched    = all.stream().filter(o -> Boolean.TRUE.equals(o.getMatchedRecommendation())).count();
        int  revenue    = all.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .mapToInt(o -> o.getFinalAmount() != null ? o.getFinalAmount() : 0)
                .sum();
        int  walletSaved = all.stream()
                .mapToInt(o -> o.getWalletDiscount() != null ? o.getWalletDiscount() : 0)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", all.size());
        stats.put("deliveredOrders", delivered);
        stats.put("pendingOrders", pending);
        stats.put("cancelledOrders", cancelled);
        stats.put("totalRevenue", revenue);
        stats.put("walletDiscountsTotal", walletSaved);
        stats.put("recommendationMatchedOrders", matched);
        return stats;
    }
}
