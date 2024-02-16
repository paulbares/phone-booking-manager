package com.phone.manager.app.domain;

import com.phone.manager.app.service.Availability;
import jakarta.persistence.*;

import java.time.Instant;


@Entity
@Table(name = "phone")
public class Phone {

  @Version // for enabling optimistic locking
  private Long version;

//  @Id
//  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
//  @SequenceGenerator(name = "sequenceGenerator")
//  private Long id;

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
    return availability;
  }

  public void setAvailability(Availability availability) {
    this.availability = availability;
  }

  public String getBorrower() {
    return borrower;
  }

  public void setBorrower(String borrower) {
    this.borrower = borrower;
  }

  public Instant getDateOfLastBooking() {
    return dateOfLastBooking;
  }

  public void setDateOfLastBooking(Instant dateOfLastBooking) {
    this.dateOfLastBooking = dateOfLastBooking;
  }

  public Instant getDateOfLastReturn() {
    return dateOfLastReturn;
  }

  public void setDateOfLastReturn(Instant dateOfReturn) {
    this.dateOfLastReturn = dateOfReturn;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Phone{");
    sb.append("version=").append(version);
    sb.append(", availability=").append(availability);
    sb.append(", name='").append(name).append('\'');
    sb.append(", borrower='").append(borrower).append('\'');
    sb.append(", dateOfBooking=").append(dateOfLastBooking);
    sb.append(", dateOfReturn=").append(dateOfLastReturn);
    sb.append('}');
    return sb.toString();
  }
}