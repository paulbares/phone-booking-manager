package com.phone.manager.app;

import java.util.List;

public final class Constants {

  /**
   * Private constructor to make sure no one will instantiate this class.
   */
  private Constants() {
  }

  /**
   * The list of phones available for testing purpose.
   */
  public static final List<String> PHONE_NAMES = List.of("Samsung Galaxy S9",
          "2x Samsung Galaxy S8",
          "Motorola Nexus 6",
          "Oneplus 9",
          "Apple iPhone 13",
          "Apple iPhone 12",
          "Apple iPhone 11",
          "iPhone X",
          "Nokia 3310");
}
