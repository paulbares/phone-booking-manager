package com.phone.manager.app.web.rest;

import com.phone.manager.app.service.Availability;

import java.time.Instant;
import java.util.Objects;

/**
 * DTO of {@link com.phone.manager.app.domain.Phone} entity. We do not expose the borrower for security reason.
 */
class PhoneDto {

  private String name;
  private Availability availability;
  private Instant dateOfLastBooking;
  private Instant dateOfLastReturn;

  /**
   * Empty constructor for jackson.
   */
  public PhoneDto() {
  }

  public PhoneDto(String name, Availability availability, Instant dateOfLastBooking, Instant dateOfLastReturn) {
    this.name = name;
    this.availability = availability;
    this.dateOfLastBooking = dateOfLastBooking;
    this.dateOfLastReturn = dateOfLastReturn;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Availability getAvailability() {
    return this.availability;
  }

  public void setAvailability(Availability availability) {
    this.availability = availability;
  }

  public Instant getDateOfLastBooking() {
    return this.dateOfLastBooking;
  }

  public void setDateOfLastBooking(Instant dateOfLastBooking) {
    this.dateOfLastBooking = dateOfLastBooking;
  }

  public Instant getDateOfLastReturn() {
    return this.dateOfLastReturn;
  }

  public void setDateOfLastReturn(Instant dateOfLastReturn) {
    this.dateOfLastReturn = dateOfLastReturn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PhoneDto phoneDto = (PhoneDto) o;
    return Objects.equals(this.name, phoneDto.name) && this.availability == phoneDto.availability && Objects.equals(this.dateOfLastBooking, phoneDto.dateOfLastBooking) && Objects.equals(this.dateOfLastReturn, phoneDto.dateOfLastReturn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.availability, this.dateOfLastBooking, this.dateOfLastReturn);
  }
}
