package com.phone.manager.app.spring.config;

import com.phone.manager.app.Constants;
import com.phone.manager.app.repository.JpaPhoneRepository;
import com.phone.manager.app.service.PhoneBookingService;
import com.phone.manager.app.service.PhoneBookingServiceImpl;
import com.phone.manager.app.spring.SpringPhoneBookingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.function.Supplier;

@Configuration
public class PhoneBookingServiceConfig {

  @Bean
  public PhoneBookingService phoneBookingService(JpaPhoneRepository repository, Supplier<Instant> timeSupplier) {
    SpringPhoneBookingService service = new SpringPhoneBookingService(new PhoneBookingServiceImpl(repository, timeSupplier));
    service.addPhones(Constants.PHONE_NAMES);
    return service;
  }

  @Bean
  public Supplier<Instant> timeSupplier() {
    return Instant::now;
  }
}
