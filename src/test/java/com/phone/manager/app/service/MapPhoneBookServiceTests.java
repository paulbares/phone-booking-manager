package com.phone.manager.app.service;

/**
 * Test class for {@link MapPhoneBookingService}.
 */
class MapPhoneBookServiceTests extends APhoneBookServiceTests {

  @Override
  protected PhoneBookingService createPhoneBookingService() {
    return new MapPhoneBookingService(() -> currentTime);
  }
}
