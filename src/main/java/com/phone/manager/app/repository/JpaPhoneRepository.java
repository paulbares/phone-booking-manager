package com.phone.manager.app.repository;

import com.phone.manager.app.domain.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaPhoneRepository extends JpaRepository<Phone, Long>, PhoneRepository {

  @Override
  default Phone saveOrUpdate(Phone phone) {
    return save(phone);
  }

  @Override
  default List<Phone> saveAllPhones(List<Phone> phones) {
    return saveAll(phones);
  }

  @Override
  default void clear() {
    deleteAll();
  }
}
