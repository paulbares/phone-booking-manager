package com.phone.manager.app.service;

import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;
import com.phone.manager.app.repository.PhoneRepository;

import java.time.Instant;
import java.util.function.Supplier;

public class PhoneBookingServiceImpl implements PhoneBookingService {

  /**
   * Domain repository.
   */
  private final PhoneRepository repository;

  /**
   * Object that can give the current time. It is particularly useful for testing purpose.
   */
  private final Supplier<Instant> timeSupplier;

  /**
   * Constructor.
   */
  public PhoneBookingServiceImpl(PhoneRepository repository, Supplier<Instant> timeSupplier) {
    this.repository = repository;
    this.timeSupplier = timeSupplier;
  }

  @Override
  public void bookPhone(String phoneName, String borrower) throws PhoneNotAvailableException, UnknownDeviceException {
    Phone phone = this.repository
            .findByName(phoneName)
            .orElseThrow(() -> new UnknownDeviceException(String.format("The phone with name %s does not exist", phoneName)));

    if (phone.getAvailability() == Availability.NO) {
      if (!phone.getBorrower().equals(borrower)) {
        throw new PhoneNotAvailableException(String.format("The phone %s is not available", phoneName));
      } else {
        return; // function is idempotent
      }
    }

    phone.setName(phoneName);
    phone.setBorrower(borrower);
    phone.setAvailability(Availability.NO);
    phone.setDateOfLastBooking(this.timeSupplier.get());
    this.repository.save(phone);
  }

  @Override
  public void returnPhone(String phoneName, String borrower) throws UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException {
    Phone phone = this.repository
            .findByName(phoneName)
            .orElseThrow(() -> new UnknownDeviceException(String.format("The phone with name %s does not exist", phoneName)));

    if (phone.getAvailability() == Availability.YES) {
      return; // Phone is already available, nothing to do
    }

    if (!phone.getBorrower().equals(borrower)) {
      throw new ReturnPhoneByIncorrectBorrowerException(String.format("The phone %s was not booked by %s", phoneName, borrower));
    }

    phone.setAvailability(Availability.YES);
    phone.setBorrower(null);
    phone.setDateOfLastReturn(this.timeSupplier.get());
    this.repository.save(phone);
  }
}
