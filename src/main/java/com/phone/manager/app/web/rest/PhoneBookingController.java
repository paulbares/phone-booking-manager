package com.phone.manager.app.web.rest;

import com.phone.manager.app.exception.PhoneNotAvailableException;
import com.phone.manager.app.exception.ReturnPhoneByIncorrectBorrowerException;
import com.phone.manager.app.exception.UnknownDeviceException;
import com.phone.manager.app.service.PhoneBookingService;
import com.phone.manager.app.service.dto.PhoneRequestDto;
import org.springframework.web.bind.annotation.*;

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
  public List<PhoneDto> getAllPhones() {
    return this.service.getAllPhones()
            .stream()
            .map(p -> new PhoneDto(p.getName(), p.getAvailability(), p.getDateOfLastBooking(), p.getDateOfLastReturn())).toList();
  }

  @PostMapping("/book")
  public String bookPhone(@RequestBody PhoneRequestDto dto) throws PhoneNotAvailableException, UnknownDeviceException {
    this.service.bookPhone(dto.getPhoneName(), dto.getBorrower());
    return "Phone " + dto.getPhoneName() + " is booked";
  }

  @PostMapping("/return")
  public String returnPhone(@RequestBody PhoneRequestDto dto) throws UnknownDeviceException, ReturnPhoneByIncorrectBorrowerException {
    this.service.returnPhone(dto.getPhoneName(), dto.getBorrower());
    return "Phone " + dto.getPhoneName() + " is returned";
  }
}
