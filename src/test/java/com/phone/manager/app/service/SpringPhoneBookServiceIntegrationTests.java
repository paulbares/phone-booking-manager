package com.phone.manager.app.service;

import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.repository.PhoneRepository;
import com.phone.manager.app.spring.conf.PhoneBookingServiceConfiguration;
import org.assertj.core.api.Assertions;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.phone.manager.app.Constants.PHONE_NAMES;

@DataJpaTest
@Import(PhoneBookingServiceConfiguration.class)
class SpringPhoneBookServiceIntegrationTests {

  private static final int NB_ITER = 50;

  @Autowired
  private PhoneBookingService service;

  @Autowired
  private PhoneRepository repository;

  @BeforeEach
  void before() {
    this.service.addPhones(PHONE_NAMES);
  }

 @AfterEach
  void after() {
   this.repository.clear();
  }

  @Test
  void testOptimisticLockingFailureException() throws Exception {
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
      roots.forEach(SpringPhoneBookServiceIntegrationTests::assertThrowable);

      List<Phone> all = this.service.getAllPhones();
      Assertions.assertThat(all.stream().filter(p -> p.getAvailability() == Availability.YES).count()).isEqualTo(PHONE_NAMES.size() - 1);
      List<Phone> bookPhones = all.stream().filter(p -> p.getAvailability() == Availability.NO).toList();
      Assertions.assertThat(bookPhones.size()).isEqualTo(1);
      Assertions.assertThat(bookPhones.iterator().next().getBorrower()).isEqualTo(winner.get());
    }
  }

  private static void assertThrowable(Throwable t) {
    if (t instanceof StaleObjectStateException) {
      Assertions.assertThat(t).hasMessageContaining("Row was updated or deleted by another transaction");
    } else if (t instanceof PhoneNotAvailableException) {
      Assertions.assertThat(t).hasMessageContaining("not available");
    } else {
      Assertions.fail(t + " was expected to be of type " + StaleObjectStateException.class
              + " or " + PhoneNotAvailableException.class
              + " but was " + t.getClass());
    }
  }

  /**
   * Gets the root cause of an error.
   * <p>
   * This particularly consider the special case where an error cause is itself. It avoids infinite loops.
   *
   * @param error error to consider
   * @return the root cause of the error
   */
  private static Throwable getRootCause(Throwable error) {
    Throwable cause;
    while ((cause = getCause(error)) != null) {
      error = cause;
    }

    return error;
  }

  /**
   * Gets the cause of an error.
   * <p>
   * This particularly consider the special case where an error cause is itself. It avoids infinite loops.
   *
   * @param error error to consider
   * @return the cause of the error or {@code null} if none
   */
  private static Throwable getCause(final Throwable error) {
    final Throwable cause = error.getCause();
    return cause == error ? null : cause;
  }
}
