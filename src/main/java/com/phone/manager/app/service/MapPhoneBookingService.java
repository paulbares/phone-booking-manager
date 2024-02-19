package com.phone.manager.app.service;

import com.phone.manager.app.domain.Availability;
import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A very simple in-memory implementation of {@link PhoneBookingService} backed by a {@link ConcurrentHashMap}.
 */
public class MapPhoneBookingService implements PhoneBookingService {

  private final Map<String, Phone> phoneByName;

  /**
   * Object that can give the current time. It is particularly useful for testing purpose.
   */
  private final Supplier<Instant> timeSupplier;

  /**
   * Constructor.
   */
  public MapPhoneBookingService(Supplier<Instant> timeSupplier) {
    this.phoneByName = new ConcurrentHashMap<>();
    this.timeSupplier = timeSupplier;
  }

  @Override
  public void bookPhone(String phoneName, String borrower) throws PhoneNotAvailableException, UnknownDeviceException {
    Exception[] ex = new Exception[1];
    this.phoneByName.compute(phoneName, (name, phone) -> {
      if (phone == null) {
        ex[0] = new UnknownDeviceException(String.format("The phone with name %s does not exist", name));
        return null;
      }

      if (phone.getAvailability() == Availability.NO) {
        if (!phone.getBorrower().equals(borrower)) {
          ex[0] = new PhoneNotAvailableException(String.format("The phone %s is not available", phoneName));
        }
        return phone; // function is idempotent
      }

      phone.setBorrower(borrower);
      phone.setAvailability(Availability.NO);
      phone.setDateOfLastBooking(this.timeSupplier.get());
      return phone;
    });

    if (ex[0] instanceof PhoneNotAvailableException pnae) {
      throw pnae;
    } else if (ex[0] instanceof UnknownDeviceException ude) {
      throw ude;
    }
  }

  @Override
  public void returnPhone(String phoneName, String borrower) throws UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException {
    Exception[] ex = new Exception[1];
    this.phoneByName.compute(phoneName, (name, phone) -> {
      if (phone == null) {
        ex[0] = new UnknownDeviceException(String.format("The phone with name %s does not exist", name));
        return null;
      }

      if (phone.getAvailability() == Availability.YES) {
        return phone; // Phone is already available, nothing to do. Function is idempotent
      }

      if (!phone.getBorrower().equals(borrower)) {
        ex[0] = new ReturnPhoneByIncorrectBorrowerException(String.format("The phone %s was not booked by %s", phoneName, borrower));
      } else {
        phone.setAvailability(Availability.YES);
        phone.setBorrower(null);
        phone.setDateOfLastReturn(this.timeSupplier.get());
      }

      return phone;
    });

    if (ex[0] instanceof ReturnPhoneByIncorrectBorrowerException e) {
      throw e;
    } else if (ex[0] instanceof UnknownDeviceException e) {
      throw e;
    }
  }

  @Override
  public Phone getPhone(String name) throws UnknownDeviceException {
    Phone p = this.phoneByName.get(name);
    if (p == null) {
      throw new UnknownDeviceException(String.format("The phone with name %s does not exist", name));
    }
    return p;
  }

  @Override
  public void addPhones(List<String> phoneNames) {
    phoneNames.forEach(name -> {
      Phone phone = new Phone();
      phone.setAvailability(Availability.YES);
      phone.setName(name);
      this.phoneByName.put(name, phone);
    });
  }

  @Override
  public List<Phone> getAllPhones() {
    return new ArrayList<>(this.phoneByName.values());
  }

  @Override
  public void clear() {
    this.phoneByName.clear();
  }
}
