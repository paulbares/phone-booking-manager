package com.phone.manager.app.service;

import com.phone.manager.app.domain.Availability;
import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;
import com.phone.manager.app.repository.PhoneRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

/**
 * Implementation of {@link PhoneBookingService} that manages the {@link Phone} entities with a {@link PhoneRepository}.
 */
public class JpaPhoneBookingService implements PhoneBookingService {

  /**
   * Repository containing the phones.
   */
  private final PhoneRepository repository;

  /**
   * Object that gives the current time. It can be replaced by a "by hand increased" time abstraction in tests.
   */
  private final Supplier<Instant> timeSupplier;

  /**
   * Constructor.
   */
  public JpaPhoneBookingService(PhoneRepository repository, Supplier<Instant> timeSupplier) {
    this.repository = repository;
    this.timeSupplier = timeSupplier;
  }

  @Override
  @Transactional
  public void bookPhone(String phoneName, String borrower) throws PhoneNotAvailableException, UnknownDeviceException {
    Phone phone = getPhone(phoneName);
    if (phone.getAvailability() == Availability.NO) {
      if (!phone.getBorrower().equals(borrower)) {
        throw new PhoneNotAvailableException(String.format("The phone %s is not available", phoneName));
      } else {
        return; // function is idempotent
      }
    }

    phone.setBorrower(borrower);
    phone.setAvailability(Availability.NO);
    phone.setDateOfLastBooking(this.timeSupplier.get());
    this.repository.save(phone);
  }

  @Override
  @Transactional
  public void returnPhone(String phoneName, String borrower) throws UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException {
    Phone phone = getPhone(phoneName);
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

  @Override
  @Transactional(readOnly = true)
  public Phone getPhone(String name) throws UnknownDeviceException {
    return this.repository.findByName(name).orElseThrow(() -> new UnknownDeviceException(String.format("The phone with name %s does not exist", name)));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void addPhones(List<String> phoneNames) {
    List<Phone> phones = phoneNames.stream().map(name -> {
      Phone phone = new Phone();
      phone.setAvailability(Availability.YES);
      phone.setName(name);
      return phone;
    }).toList();
    this.repository.saveAll(phones);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void clear() {
    this.repository.deleteAll();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Phone> getAllPhones() {
    return this.repository.findAll();
  }
}
