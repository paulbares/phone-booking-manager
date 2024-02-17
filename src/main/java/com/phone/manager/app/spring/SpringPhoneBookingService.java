package com.phone.manager.app.spring;

import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;
import com.phone.manager.app.service.PhoneBookingService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * A Spring oriented class with Spring annotations that delegates of calls to a n underlying {@link PhoneBookingService}
 * that is Spring agnostic.
 */
public class SpringPhoneBookingService implements PhoneBookingService {

  /**
   * The underlying {@link PhoneBookingService}.
   */
  private final PhoneBookingService underlying;

  public SpringPhoneBookingService(PhoneBookingService underlying) {
    this.underlying = underlying;
  }

  @Override
  @Transactional
  public void bookPhone(String phoneName, String borrower) throws PhoneNotAvailableException, UnknownDeviceException {
    this.underlying.bookPhone(phoneName, borrower);
  }

  @Override
  @Transactional
  public void returnPhone(String phoneName, String borrower) throws UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException {
    this.underlying.returnPhone(phoneName, borrower);
  }

  @Override
  @Transactional(readOnly = true)
  public Phone getPhone(String name) throws UnknownDeviceException {
    return this.underlying.getPhone(name);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Phone> getAllPhones() {
    return this.underlying.getAllPhones();
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void addPhones(List<String> phoneNames) {
    this.underlying.addPhones(phoneNames);
  }
}
