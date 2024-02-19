package com.phone.manager.app.service;

import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;

import java.util.List;

/**
 * The {@code PhoneBookingService} interface defines the contract for storing and managing {@link Phone} entities.
 * <p>
 * The interface is designed to provide a common set of methods for interacting with an underlying storage
 * mechanism, whether it be a relational database, a NoSQL database, or any other data store or data structure.
 * </p>
 */
public interface PhoneBookingService {

  /**
   * Books a phone with the given name of the given borrower. Only one user at a time can book a phone. Calling this
   * method multiple times with the same parameters should not have any side effect i.e. it should be idempotent.
   *
   * @param phoneName the name of the phone
   * @param borrower  the name of the user that wants to borrow the phone
   * @throws UnknownDeviceException     if the phone does not exist
   * @throws PhoneNotAvailableException if the phone is not available i.e. already borrowed by another user
   */
  void bookPhone(String phoneName, String borrower) throws PhoneNotAvailableException, UnknownDeviceException;

  /**
   * Returns the phone with the given name that was previously borrowed by the given user. Calling this method multiple
   * times with the same parameters should not have any side effect i.e. it should be idempotent.
   *
   * @param phoneName the name of the phone
   * @param borrower  the name of the user that wants to return the phone
   * @throws UnknownDeviceException                  if the phone does not exist
   * @throws ReturnPhoneByIncorrectBorrowerException if the phone is borrowed by the user trying to return it is not
   *                                                 the one that borrowed it
   */
  void returnPhone(String phoneName, String borrower) throws UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException;

  /**
   * Retrieves a {@link Phone} entity by its name.
   *
   * @param name the name of the phone.
   * @return the {@link Phone} entity with the given name.
   * @throws UnknownDeviceException if there is no phone with this name.
   */
  Phone getPhone(String name) throws UnknownDeviceException;

  /**
   * Retrieves all phones.
   * <p>
   * The list contains both phones that are available ({@link Phone#getAvailability()} == {@link Availability#YES})
   * or not ({@link Phone#getAvailability()} == {@link Availability#NO}).
   * </p>
   *
   * @return the phones.
   */
  List<Phone> getAllPhones();

  /**
   * Adds new phones available afterward.
   *
   * @param phoneNames the list of phones designated by their names.
   */
  void addPhones(List<String> phoneNames);

  /**
   * Deletes all phones.
   */
  void clear();
}
