package com.phone.manager.app.exception;

/**
 * Thrown when someone tries to borrow a phone already borrowed by someone else.
 */
public class PhoneNotAvailableException extends Exception {

  public PhoneNotAvailableException(String message) {
    super(message);
  }
}
