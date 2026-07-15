package com.krushna.commercecore.repository;

import com.krushna.commercecore.model.Notification;
import com.krushna.commercecore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    int countByUserAndIsReadFalse(User user);
    void deleteByUser(User user);
}
