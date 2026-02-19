// NEW_FILE_CODE
package com.google.rating.saas_ai.repository;

import com.google.rating.saas_ai.entity.Subscription;
import com.google.rating.saas_ai.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByBusiness(Business business);
    List<Subscription> findByStatusAndEndDateBefore(Subscription.SubscriptionStatus status, LocalDateTime date);
}
