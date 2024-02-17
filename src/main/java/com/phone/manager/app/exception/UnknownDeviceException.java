package com.phone.manager.app.exception;

/**
 * Thrown when someone tries to borrow or return a phone that does not exist.
 */
public class UnknownDeviceException extends Exception {

  public UnknownDeviceException(String message) {
    super(message);
  }
}
