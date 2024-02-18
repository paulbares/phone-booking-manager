package com.phone.manager.app.service;

import com.phone.manager.app.TestUtil;
import com.phone.manager.app.domain.Phone;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.phone.manager.app.Constants.PHONE_NAMES;

/**
 * Test class for {@link MapPhoneBookingService}.
 */
class MapPhoneBookServiceTests extends APhoneBookServiceTests {

  @Override
  protected PhoneBookingService createPhoneBookingService() {
    return new MapPhoneBookingService(() -> this.currentTime);
  }

  @Test
  void testOptimisticLockingFailureException() throws Exception {
    int nThreads = Runtime.getRuntime().availableProcessors();

    // Run the test multiple times to increase the probability to enter in a "bad" state i.e
    // a state where at least two threads try to concurrently save the same entity.
    for (int iter = 0; iter < 100; iter++) {

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
        roots.add(TestUtil.getRootCause(e));
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
