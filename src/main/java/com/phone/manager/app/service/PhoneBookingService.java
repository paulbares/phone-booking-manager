package com.phone.manager.app.service;

import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;
import com.phone.manager.app.repository.PhoneRepository;

import java.util.List;

public interface PhoneBookingService {

  void bookPhone(String phoneName, String borrower) throws PhoneNotAvailableException, UnknownDeviceException;

  void returnPhone(String phoneName, String borrower) throws UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException;

  /**
   * Retrieves a {@link Phone} entity by its name from the {@link PhoneRepository}.
   *
   * @param name the name of the phone.
   * @return the {@link Phone} entity with the given name.
   * @throws UnknownDeviceException if there is no phone with this name.
   */
  Phone getPhone(String name) throws UnknownDeviceException;

  /**
   * Retrieves all phones stored in the {@link PhoneRepository}.
   * <p>
   * The list contains both phones that are available ({@link Phone#getAvailability()} == {@link Availability#YES})
   * or not ({@link Phone#getAvailability()} == {@link Availability#NO}).
   * </p>
   *
   * @return the phones.
   */
  List<Phone> getAllPhones();

  /**
   * Adds new phones to the {@link PhoneRepository}.
   *
   * @param phoneNames the list of phones designated by their names to store in the {@link PhoneRepository}.
   */
  void addPhones(List<String> phoneNames);
}
