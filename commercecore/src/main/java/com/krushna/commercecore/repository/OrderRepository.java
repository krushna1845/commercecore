package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.Order;
import com.krushna.commercecore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByStripePaymentIntentId(String stripePaymentIntentId);

    List<Order> findByUser(User user);
}
