package com.phone.manager.app.repository;

import com.phone.manager.app.domain.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

  Optional<Phone> findByName(String phoneName);
}
