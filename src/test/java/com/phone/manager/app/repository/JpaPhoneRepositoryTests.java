package com.phone.manager.app.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class JpaPhoneRepositoryTests {

  @Autowired
  private JpaPhoneRepository repository;

//  @Test
//  void testDuplicate() {
//    // Here we are making sure the name of the phone is uniquely identifying a device.
//    Phone phone = new Phone();
//    phone.setAvailability(Availability.YES);
//    phone.setName(PHONE_NAMES.get(0));
//    this.repository.saveOrUpdate(phone);
//    Assertions.assertThatThrownBy(() -> this.repository.saveOrUpdate(phone)).isInstanceOf(DuplicateKeyException.class);
//  }
}
