package com.phone.manager.app;

import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;
import com.phone.manager.app.repository.PhoneRepository;
import com.phone.manager.app.service.Availability;
import com.phone.manager.app.service.PhoneBookingService;
import com.phone.manager.app.service.PhoneBookingServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@DataJpaTest
@Transactional
class PhoneBookServiceTests {

  private static final Instant NOW = Instant.now();

  private static final List<String> PHONE_NAMES = List.of("Samsung Galaxy S9",
          "2x Samsung Galaxy S8",
          "Motorola Nexus 6",
          "Oneplus 9",
          "Apple iPhone 13",
          "Apple iPhone 12",
          "Apple iPhone 11",
          "iPhone X",
          "Nokia 3310");

  @Autowired
  private PhoneRepository repository;

  private PhoneBookingService service;

  private Instant currentTime = NOW; // default value

  @BeforeEach
  void before() {
    for (String phoneName : PHONE_NAMES) {
      Phone phone = new Phone();
      phone.setAvailability(Availability.YES);
      phone.setName(phoneName);
      repository.save(phone);
    }
    service = new PhoneBookingServiceImpl(repository, () -> currentTime);
  }

  @Test
  void testDuplicate() {
    // Here we are making sure the name of the phone is uniquely identifying a device.
    Phone phone = new Phone();
    phone.setAvailability(Availability.YES);
    phone.setName(PHONE_NAMES.get(0));
    Assertions.assertThatThrownBy(() -> repository.save(phone)).isInstanceOf(DuplicateKeyException.class);
  }

  @Test
  void testBookUnknownPhone() {
    Assertions.assertThatThrownBy(() -> service.bookPhone("unknown", "peter")).isInstanceOf(UnknownDeviceException.class);
  }

  @Test
  void testBookPhoneNotAvailable() throws PhoneNotAvailableException, UnknownDeviceException {
    String phoneName = PHONE_NAMES.get(0);
    service.bookPhone(phoneName, "peter"); // book first phone
    Assertions.assertThatThrownBy(() -> service.bookPhone(phoneName, "paul")).isInstanceOf(PhoneNotAvailableException.class);
  }

  @Test
  void testBookPhoneIdempotence() throws PhoneNotAvailableException, UnknownDeviceException {
    String phoneName = PHONE_NAMES.get(0);
    service.bookPhone(phoneName, "peter");
    service.bookPhone(phoneName, "peter"); // should not throw when the same user is booking a phone already booked
  }

  @Test
  void testReturnUnknownPhone() {
    Assertions.assertThatThrownBy(() -> service.returnPhone("unknown", "peter")).isInstanceOf(UnknownDeviceException.class);
  }

  @Test
  void testReturnPhoneByIncorrectBorrower() throws PhoneNotAvailableException, UnknownDeviceException {
    String phoneName = PHONE_NAMES.get(0);
    service.bookPhone(phoneName, "peter");
    Assertions.assertThatThrownBy(() -> service.returnPhone(phoneName, "paul")).isInstanceOf(ReturnPhoneByIncorrectBorrowerException.class);

    // Make sure it did not affect the db entity
    Assertions.assertThat(repository.findByName(phoneName).get().getAvailability()).isEqualTo(Availability.NO);
  }

  @Test
  void testReturnPhoneIdempotence() throws PhoneNotAvailableException, UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException {
    String phoneName = PHONE_NAMES.get(0);
    String peter = "peter";
    service.bookPhone(phoneName, peter);
    service.returnPhone(phoneName, peter);
    service.returnPhone(phoneName, peter);
  }

  @Test
  void testBookAndReturn() throws PhoneNotAvailableException, UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException {
    List<Phone> all = repository.findAll();
    // Check that all phones are available
    Assertions.assertThat(all.stream().filter(p -> p.getAvailability() == Availability.YES).count()).isEqualTo(PHONE_NAMES.size());

    service.bookPhone(PHONE_NAMES.get(0), "peter");
    // Change the time to make sure it is correctly recorded
    currentTime = Instant.now();
    service.bookPhone(PHONE_NAMES.get(5), "paul");

    Assertions.assertThat(all.stream().filter(p -> p.getAvailability() == Availability.YES).count()).isEqualTo(PHONE_NAMES.size() - 2);
    Phone petersPhone = repository.findByName(PHONE_NAMES.get(0)).get();
    Phone paulsPhone = repository.findByName(PHONE_NAMES.get(5)).get();
    Assertions.assertThat(petersPhone.getAvailability()).isEqualTo(Availability.NO);
    Assertions.assertThat(petersPhone.getBorrower()).isEqualTo("peter");
    Assertions.assertThat(petersPhone.getDateOfLastBooking()).isEqualTo(NOW);
    Assertions.assertThat(petersPhone.getDateOfLastReturn()).isNull();

    Assertions.assertThat(paulsPhone.getAvailability()).isEqualTo(Availability.NO);
    Assertions.assertThat(paulsPhone.getBorrower()).isEqualTo("paul");
    Assertions.assertThat(paulsPhone.getDateOfLastBooking()).isEqualTo(currentTime);
    Assertions.assertThat(paulsPhone.getDateOfLastReturn()).isNull();

    // Change the time to make sure it is correctly recorded
    Instant lastBooking = currentTime;
    currentTime = Instant.now();
    service.returnPhone(PHONE_NAMES.get(5), "paul");
    paulsPhone = repository.findByName(PHONE_NAMES.get(5)).get();
    Assertions.assertThat(paulsPhone.getAvailability()).isEqualTo(Availability.YES);
    Assertions.assertThat(paulsPhone.getBorrower()).isNull();
    Assertions.assertThat(paulsPhone.getDateOfLastBooking()).isEqualTo(lastBooking);
    Assertions.assertThat(paulsPhone.getDateOfLastReturn()).isEqualTo(currentTime);
  }
}
