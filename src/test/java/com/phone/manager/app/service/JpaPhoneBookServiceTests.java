package com.phone.manager.app.service;

import com.phone.manager.app.repository.JpaPhoneRepository;
import com.phone.manager.app.spring.SpringPhoneBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Test class for {@link PhoneBookingService} using a {@link JpaPhoneRepository}.
 */
@DataJpaTest
@Import(JpaPhoneBookServiceTests.JpaPhoneBookServiceTestConfiguration.class)
class JpaPhoneBookServiceTests extends APhoneBookServiceTests {

  @TestConfiguration
  static class JpaPhoneBookServiceTestConfiguration {

    @Bean
    public PhoneBookingService phoneBookingService(JpaPhoneRepository repository) {
      return new SpringPhoneBookingService(new PhoneBookingServiceImpl(repository, () -> currentTime));
    }

//    @Bean
//    @Primary
//    public Supplier<Instant> timeSupplierForTest() {
//      return () -> currentTime;
//    }
  }

  @Autowired
  private PhoneBookingService service;

  @Override
  protected PhoneBookingService createPhoneBookingService() {
    return this.service;
  }
}
