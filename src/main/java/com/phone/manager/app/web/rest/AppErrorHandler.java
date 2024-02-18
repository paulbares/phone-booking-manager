package com.phone.manager.app.web.rest;

import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@Order(1)
public class AppErrorHandler {

  @ExceptionHandler(value = UnknownDeviceException.class)
  @ResponseBody
  public ResponseEntity<String> unknownDevice() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("phone does not exist");
  }

  @ExceptionHandler(value = ReturnPhoneByIncorrectBorrowerException.class)
  @ResponseBody
  public ResponseEntity<String> returnIncorrectBorrower() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("try to return a phone not borrowed");
  }
}
