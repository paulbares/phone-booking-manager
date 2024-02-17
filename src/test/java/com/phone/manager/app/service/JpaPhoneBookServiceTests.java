package com.phone.manager.app.service;

import com.phone.manager.app.repository.JpaPhoneRepository;
import com.phone.manager.app.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * Test class for {@link PhoneBookingService} using a {@link JpaPhoneRepository}.
 */
@DataJpaTest
class JpaPhoneBookServiceTests extends APhoneBookServiceTests {

  @Autowired
  private JpaPhoneRepository jpaPhoneRepository;

  @Override
  protected PhoneRepository createPhoneRepository() {
    return this.jpaPhoneRepository;
  }
}
