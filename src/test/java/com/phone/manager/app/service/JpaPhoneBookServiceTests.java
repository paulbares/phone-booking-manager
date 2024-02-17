package com.phone.manager.app.service;

import com.phone.manager.app.repository.JpaPhoneRepository;
import com.phone.manager.app.repository.PhoneRepository;
import com.phone.manager.app.spring.conf.PhoneBookingServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * Test class for {@link PhoneBookingService} using a {@link JpaPhoneRepository}.
 */
@DataJpaTest
@Import(PhoneBookingServiceConfiguration.class)
class JpaPhoneBookServiceTests extends APhoneBookServiceTests {

  @Autowired
  private JpaPhoneRepository jpaPhoneRepository;

  @Override
  protected PhoneRepository createPhoneRepository() {
    return this.jpaPhoneRepository;
  }
}
