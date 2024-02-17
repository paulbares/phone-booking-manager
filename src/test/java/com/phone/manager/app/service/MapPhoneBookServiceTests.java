package com.phone.manager.app.service;

import com.phone.manager.app.repository.MapPhoneRepository;
import com.phone.manager.app.repository.PhoneRepository;

/**
 * Test class for {@link PhoneBookingService} using a {@link MapPhoneRepository}.
 */
class MapPhoneBookServiceTests extends APhoneBookServiceTests {

  @Override
  protected PhoneRepository createPhoneRepository() {
    return new MapPhoneRepository();
  }
}
