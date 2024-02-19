package com.phone.manager.app.service;

/**
 * Test class for the {@link MapPhoneBookingService} implementation of {@link PhoneBookingService}.
 */
class MapPhoneBookingServiceTests extends APhoneBookingServiceTests {

  @Override
  protected PhoneBookingService createPhoneBookingService() {
    return new MapPhoneBookingService(() -> currentTime);
  }
}
