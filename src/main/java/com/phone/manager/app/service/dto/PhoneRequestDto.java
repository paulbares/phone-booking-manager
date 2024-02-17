package com.phone.manager.app.service.dto;

public class PhoneRequestDto {

  private String phoneName;
  private String borrower;

  public PhoneRequestDto() {
  }

  public PhoneRequestDto(String phoneName, String borrower) {
    this.phoneName = phoneName;
    this.borrower = borrower;
  }

  public String getPhoneName() {
    return this.phoneName;
  }

  public void setPhoneName(String phoneName) {
    this.phoneName = phoneName;
  }

  public String getBorrower() {
    return this.borrower;
  }

  public void setBorrower(String borrower) {
    this.borrower = borrower;
  }
}
