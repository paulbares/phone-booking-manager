package com.phone.manager.app.service;

import com.phone.manager.app.TestUtil;
import com.phone.manager.app.domain.Availability;
import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.phone.manager.app.Constants.PHONE_NAMES;
import static com.phone.manager.app.TestUtil.getRootCause;

/**
 * Base class that contains tests for {@link PhoneBookingService}.
 */
abstract class APhoneBookingServiceTests {

  private static final Instant NOW = Instant.now();

  private static final int NB_ITER = 50;

  /**
   * The instance created with {@link #createPhoneBookingService()}.
   */
  protected PhoneBookingService service;

  /**
   * The current instant for the test.
   */
  protected static Instant currentTime;

  /**
   * Creates the {@link PhoneBookingService}. Sub-classes have to implement this method to test specific implementation.
   *
   * @return the {@link PhoneBookingService} to be tested.
   */
  protected abstract PhoneBookingService createPhoneBookingService();

  @BeforeEach
  void before() {
    this.service = createPhoneBookingService();
    this.service.addPhones(PHONE_NAMES);
    currentTime = NOW;
  }

  @AfterEach
  void after() {
    this.service.clear();
  }

  @Test
  void testGetUnknownPhone() {
    Assertions.assertThatThrownBy(() -> this.service.getPhone("unknown")).isInstanceOf(UnknownDeviceException.class);
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
    Assertions.assertThat(this.service.getPhone(phoneName).getAvailability()).isEqualTo(Availability.NO);
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
    currentTime = Instant.now();
    this.service.bookPhone(PHONE_NAMES.get(5), "paul");

    all = this.service.getAllPhones();
    Assertions.assertThat(all.stream().filter(p -> p.getAvailability() == Availability.YES).count()).isEqualTo(PHONE_NAMES.size() - 2);
    Phone petersPhone = this.service.getPhone(PHONE_NAMES.get(0));
    Assertions.assertThat(petersPhone.getAvailability()).isEqualTo(Availability.NO);
    Assertions.assertThat(petersPhone.getBorrower()).isEqualTo("peter");
    Assertions.assertThat(petersPhone.getDateOfLastBooking()).isEqualTo(NOW);
    Assertions.assertThat(petersPhone.getDateOfLastReturn()).isNull();

    Phone paulsPhone = this.service.getPhone(PHONE_NAMES.get(5));
    Assertions.assertThat(paulsPhone.getAvailability()).isEqualTo(Availability.NO);
    Assertions.assertThat(paulsPhone.getBorrower()).isEqualTo("paul");
    Assertions.assertThat(paulsPhone.getDateOfLastBooking()).isEqualTo(currentTime);
    Assertions.assertThat(paulsPhone.getDateOfLastReturn()).isNull();

    Instant lastBooking = currentTime;
    // Change the time to make sure it is correctly recorded
    currentTime = Instant.now();
    this.service.returnPhone(PHONE_NAMES.get(5), "paul");
    paulsPhone = this.service.getPhone(PHONE_NAMES.get(5));
    Assertions.assertThat(paulsPhone.getAvailability()).isEqualTo(Availability.YES);
    Assertions.assertThat(paulsPhone.getBorrower()).isNull();
    Assertions.assertThat(paulsPhone.getDateOfLastBooking()).isEqualTo(lastBooking);
    Assertions.assertThat(paulsPhone.getDateOfLastReturn()).isEqualTo(currentTime);
  }

  @Test
  void testConcurrentBooking() throws Exception {
    int nThreads = Runtime.getRuntime().availableProcessors();

    // Run the test multiple times to increase the probability to enter in a "bad" state i.e
    // a state where at least two threads try to concurrently save the same entity.
    for (int iter = 0; iter < NB_ITER; iter++) {

      // N = nThreads will compete to book the same phone, we store the borrower here
      AtomicReference<String> winner = new AtomicReference<>();
      List<Future<?>> futures = new ArrayList<>(nThreads);
      ExecutorService executor = Executors.newFixedThreadPool(nThreads);
      List<Throwable> roots = new ArrayList<>(); // The exceptions thrown.

      // Start the booking competition
      for (int i = 0; i < nThreads; i++) {
        int id = i;
        futures.add(executor.submit(() -> {
          try {
            String borrower = "peter_" + id;
            this.service.bookPhone(PHONE_NAMES.get(0), borrower);
            if (!winner.compareAndSet(null, borrower)) {
              throw new IllegalStateException("A value already exist. This is not expected. Please investigate to find the issue");
            }
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }));
      }

      try {
        for (Future<?> future : futures) {
          future.get();
        }
      } catch (Exception e) {
        // Swallow the exception. The root cause exception will be checked below.
        roots.add(getRootCause(e));
      }

      executor.shutdown();
      executor.awaitTermination(1, TimeUnit.MINUTES);

      // Assert the exceptions
      roots.forEach(TestUtil::assertThrowable);

      List<Phone> all = this.service.getAllPhones();
      Assertions.assertThat(all.stream().filter(p -> p.getAvailability() == Availability.YES).count()).isEqualTo(PHONE_NAMES.size() - 1);
      List<Phone> bookPhones = all.stream().filter(p -> p.getAvailability() == Availability.NO).toList();
      Assertions.assertThat(bookPhones.size()).isEqualTo(1);
      Assertions.assertThat(bookPhones.iterator().next().getBorrower()).isEqualTo(winner.get());
    }
  }
}
