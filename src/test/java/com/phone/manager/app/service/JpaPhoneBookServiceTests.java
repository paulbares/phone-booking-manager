package com.phone.manager.app.service;

import com.phone.manager.app.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Test class for the {@link JpaPhoneBookingService} implementation of {@link PhoneBookingService}.
 */
@DataJpaTest
@Import(JpaPhoneBookServiceTests.JpaPhoneBookServiceTestConfiguration.class)
class JpaPhoneBookServiceTests extends APhoneBookServiceTests {

  @TestConfiguration
  static class JpaPhoneBookServiceTestConfiguration {
    @Bean
    public PhoneBookingService phoneBookingService(PhoneRepository repository) {
      return new JpaPhoneBookingService(repository, () -> currentTime);
    }
  }

  @Autowired
  private PhoneBookingService service;

  @Override
  protected PhoneBookingService createPhoneBookingService() {
    return this.service;
  }
}
