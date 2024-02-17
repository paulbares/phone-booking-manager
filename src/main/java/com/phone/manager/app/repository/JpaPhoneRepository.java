package com.phone.manager.app.repository;

import com.phone.manager.app.domain.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the {@link Phone} entity.
 */
@Repository
public interface JpaPhoneRepository extends JpaRepository<Phone, Long>, PhoneRepository {

  @Override
  default void saveOrUpdate(Phone phone) {
    save(phone);
  }

  @Override
  default void saveAllPhones(List<Phone> phones) {
    saveAll(phones);
  }

  @Override
  default void clear() {
    deleteAll();
  }
}
