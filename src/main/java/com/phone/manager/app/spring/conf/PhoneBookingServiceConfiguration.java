package com.phone.manager.app.spring.conf;

import com.phone.manager.app.repository.JpaPhoneRepository;
import com.phone.manager.app.service.PhoneBookingService;
import com.phone.manager.app.service.PhoneBookingServiceImpl;
import com.phone.manager.app.spring.SpringPhoneBookingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class PhoneBookingServiceConfiguration {

  @Bean
  public PhoneBookingService phoneBookingService(JpaPhoneRepository repository) {
    return new SpringPhoneBookingService(new PhoneBookingServiceImpl(repository, Instant::now));
  }
}
