// NEW_FILE_CODE
package com.google.rating.saas_ai.repository;

import com.google.rating.saas_ai.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findByGooglePlaceId(String googlePlaceId);
    Optional<Business> findByBusinessEmail(String businessEmail);
}
