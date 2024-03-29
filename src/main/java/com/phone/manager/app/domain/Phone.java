package com.phone.manager.app.domain;

import jakarta.persistence.*;

import java.time.Instant;


@Entity
@Table(name = "phone")
public class Phone {

  @Version // for enabling optimistic locking
  private Long version;

  @Id
  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Availability availability;

  @Column
  private String borrower;

  @Column(name = "date_of_last_booking")
  private Instant dateOfLastBooking;

  @Column(name = "date_of_last_return")
  private Instant dateOfLastReturn;

  public Availability getAvailability() {
    return this.availability;
  }

  public void setAvailability(Availability availability) {
    this.availability = availability;
  }

  public String getBorrower() {
    return this.borrower;
  }

  public void setBorrower(String borrower) {
    this.borrower = borrower;
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

  public void setDateOfLastReturn(Instant dateOfReturn) {
    this.dateOfLastReturn = dateOfReturn;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }
}