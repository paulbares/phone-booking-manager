package com.phone.manager.app.web.rest;

import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;
import com.phone.manager.app.service.PhoneBookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PhoneBookingController {

  private final PhoneBookingService service;

  public PhoneBookingController(PhoneBookingService service) {
    this.service = service;
  }

  /**
   * {@code GET  /account} : get the current user.
   *
   * @return the current user.
   */
  @GetMapping("/phones")
  public ResponseEntity<List<PhoneDto>> getAllPhones() {
    List<PhoneDto> phones = this.service.getAllPhones()
            .stream()
            .map(p -> new PhoneDto(p.getName(), p.getAvailability(), p.getDateOfLastBooking(), p.getDateOfLastReturn())).toList();
    return ResponseEntity.ok(phones);
  }

  @PostMapping("/book")
  public String bookPhone(@RequestBody String name, Principal principal) throws PhoneNotAvailableException, UnknownDeviceException {
    this.service.bookPhone(name, principal.getName());
    return "Phone " + name + " is booked";
  }

  @PostMapping("/return")
  public String returnPhone(@RequestBody String name, Principal principal) throws UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException {
    this.service.returnPhone(name, principal.getName());
    return "Phone " + name + " is returned";
  }
}
