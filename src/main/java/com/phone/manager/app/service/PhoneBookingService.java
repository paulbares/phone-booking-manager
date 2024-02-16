package com.phone.manager.app.service;

import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;

public interface PhoneBookingService {

  void bookPhone(String phoneName, String borrower) throws PhoneNotAvailableException, UnknownDeviceException;

  void returnPhone(String phoneName, String borrower) throws UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException;
}
