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
  public Phone saveOrUpdate(Phone phone) {
    return this.phoneByName.computeIfAbsent(phone.getName(), __ -> phone);
  }

  @Override
  public List<Phone> findAll() {
    return new ArrayList<>(this.phoneByName.values());
  }

  @Override
  public List<Phone> saveAllPhones(List<Phone> phones) {
    return phones.stream().map(this::saveOrUpdate).toList();
  }

  @Override
  public Optional<Phone> findByName(String name) {
    return Optional.ofNullable(this.phoneByName.get(name));
  }

  @Override
  public void clear() {
    this.phoneByName.clear();
  }
}
