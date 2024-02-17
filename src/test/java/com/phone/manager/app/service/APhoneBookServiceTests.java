package com.phone.manager.app.service;

import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;
import com.phone.manager.app.repository.PhoneRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.phone.manager.app.Constants.PHONE_NAMES;

/**
 * Base class that contains tests for {@link PhoneBookingService}.
 */
@Transactional // annotation here because visibility of the tests (private)
abstract class APhoneBookServiceTests {

  private static final Instant NOW = Instant.now();

  private PhoneRepository repository;

  private PhoneBookingService service;

  /**
   * The current instant for the test.
   */
  private Instant currentTime = NOW; // default value

  protected abstract PhoneRepository createPhoneRepository();

  @BeforeEach
  void before() {
    this.repository = createPhoneRepository();
    this.service = new PhoneBookingServiceImpl(this.repository, () -> this.currentTime);
    this.service.addPhones(PHONE_NAMES);
  }

  @AfterEach
  void after() {
    this.repository.clear();
  }

  @Test
  void testBookUnknownPhone() {
    Assertions.assertThatThrownBy(() -> this.service.bookPhone("unknown", "peter")).isInstanceOf(UnknownDeviceException.class);
  }

  @Test
  void testBookPhoneNotAvailable() throws PhoneNotAvailableException, UnknownDeviceException {
    String phoneToBook = PHONE_NAMES.get(0);
    // book first phone by peter
    this.service.bookPhone(phoneToBook, "peter");
    // try to book the same phone by a different user
    Assertions.assertThatThrownBy(() -> this.service.bookPhone(phoneToBook, "paul")).isInstanceOf(PhoneNotAvailableException.class);
  }

  @Test
  void testBookPhoneIdempotence() throws PhoneNotAvailableException, UnknownDeviceException {
    String phoneName = PHONE_NAMES.get(0);
    this.service.bookPhone(phoneName, "peter");
    this.service.bookPhone(phoneName, "peter"); // should not throw when the same user is booking a phone already booked
  }

  @Test
  void testReturnUnknownPhone() {
    Assertions.assertThatThrownBy(() -> this.service.returnPhone("unknown", "peter")).isInstanceOf(UnknownDeviceException.class);
  }

  @Test
  void testReturnPhoneByIncorrectBorrower() throws PhoneNotAvailableException, UnknownDeviceException {
    String phoneName = PHONE_NAMES.get(0);
    this.service.bookPhone(phoneName, "peter");
    Assertions.assertThatThrownBy(() -> this.service.returnPhone(phoneName, "paul")).isInstanceOf(ReturnPhoneByIncorrectBorrowerException.class);

    // Make sure it did not affect the db entity
    Assertions.assertThat(this.repository.findByName(phoneName).get().getAvailability()).isEqualTo(Availability.NO);
  }

  @Test
  void testReturnPhoneNotBooked() {
    Assertions.assertThatNoException().isThrownBy(() -> this.service.returnPhone(PHONE_NAMES.get(0), "peter"));
  }

  @Test
  void testBookAndReturn() throws PhoneNotAvailableException, UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException {
    List<Phone> all = this.service.getAllPhones();
    // Check that all phones are available
    Assertions.assertThat(all.stream().filter(p -> p.getAvailability() == Availability.YES).count()).isEqualTo(PHONE_NAMES.size());

    this.service.bookPhone(PHONE_NAMES.get(0), "peter");
    // Change the time to make sure it is correctly recorded
    this.currentTime = Instant.now();
    this.service.bookPhone(PHONE_NAMES.get(5), "paul");

    all = this.service.getAllPhones();
    Assertions.assertThat(all.stream().filter(p -> p.getAvailability() == Availability.YES).count()).isEqualTo(PHONE_NAMES.size() - 2);
    Phone petersPhone = this.repository.findByName(PHONE_NAMES.get(0)).get();
    Phone paulsPhone = this.repository.findByName(PHONE_NAMES.get(5)).get();
    Assertions.assertThat(petersPhone.getAvailability()).isEqualTo(Availability.NO);
    Assertions.assertThat(petersPhone.getBorrower()).isEqualTo("peter");
    Assertions.assertThat(petersPhone.getDateOfLastBooking()).isEqualTo(NOW);
    Assertions.assertThat(petersPhone.getDateOfLastReturn()).isNull();

    Assertions.assertThat(paulsPhone.getAvailability()).isEqualTo(Availability.NO);
    Assertions.assertThat(paulsPhone.getBorrower()).isEqualTo("paul");
    Assertions.assertThat(paulsPhone.getDateOfLastBooking()).isEqualTo(this.currentTime);
    Assertions.assertThat(paulsPhone.getDateOfLastReturn()).isNull();

    Instant lastBooking = this.currentTime;
    // Change the time to make sure it is correctly recorded
    this.currentTime = Instant.now();
    this.service.returnPhone(PHONE_NAMES.get(5), "paul");
    paulsPhone = this.repository.findByName(PHONE_NAMES.get(5)).get();
    Assertions.assertThat(paulsPhone.getAvailability()).isEqualTo(Availability.YES);
    Assertions.assertThat(paulsPhone.getBorrower()).isNull();
    Assertions.assertThat(paulsPhone.getDateOfLastBooking()).isEqualTo(lastBooking);
    Assertions.assertThat(paulsPhone.getDateOfLastReturn()).isEqualTo(this.currentTime);
  }
}
