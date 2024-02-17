package com.phone.manager.app.exception;

/**
 * Thrown when someone tries to return a phone she did not borrow.
 */
public class ReturnPhoneByIncorrectBorrowerException extends Exception {

  public ReturnPhoneByIncorrectBorrowerException(String message) {
    super(message);
  }
}
