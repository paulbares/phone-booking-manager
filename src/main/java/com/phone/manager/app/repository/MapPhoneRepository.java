package com.phone.manager.app.repository;

import com.phone.manager.app.domain.Phone;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A very simple in-memory implementation of {@link PhoneRepository} backed by a {@link ConcurrentHashMap}.
 */
public class MapPhoneRepository implements PhoneRepository {

  private final Map<String, Phone> phoneByName = new ConcurrentHashMap<>();

  @Override
  public void saveOrUpdate(Phone phone) {
    this.phoneByName.put(phone.getName(), phone);
  }

  @Override
  public List<Phone> findAll() {
    return new ArrayList<>(this.phoneByName.values());
  }

  @Override
  public void saveAllPhones(List<Phone> phones) {
    phones.forEach(this::saveOrUpdate);
  }

  @Override
  public Optional<Phone> findByName(String name) {
    Phone p = this.phoneByName.get(name);
    return Optional.ofNullable(p == null ? null : p.clone());
  }

  @Override
  public void clear() {
    this.phoneByName.clear();
  }
}
