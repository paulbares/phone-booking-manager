package com.phone.manager.app.repository;

import com.phone.manager.app.domain.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository for the {@link Phone} entity.
 */
@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

  /**
   * Retrieves an entity by its name.
   *
   * @param name must not be {@literal null}.
   * @return the entity with the given name or {@literal Optional#empty()} if none found.
   */
  Optional<Phone> findByName(String name);
}
