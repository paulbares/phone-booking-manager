package com.phone.manager.app.spring.config;

import com.phone.manager.app.Constants;
import com.phone.manager.app.repository.PhoneRepository;
import com.phone.manager.app.service.JpaPhoneBookingService;
import com.phone.manager.app.service.PhoneBookingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.function.Supplier;

@Configuration
public class PhoneBookingServiceConfig {

  @Bean
  public PhoneBookingService phoneBookingService(PhoneRepository repository, Supplier<Instant> timeSupplier) {
    PhoneBookingService service = new JpaPhoneBookingService(repository, timeSupplier);
    service.addPhones(Constants.PHONE_NAMES);
    return service;
  }

  @Bean
  public Supplier<Instant> timeSupplier() {
    return Instant::now;
  }
}
