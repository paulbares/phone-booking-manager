package com.phone.manager.app.repository;

import com.phone.manager.app.domain.Phone;

import java.util.List;
import java.util.Optional;

/**
 * The {@code PhoneRepository} interface defines the contract for a repository that stores and manages {@link Phone} entities.
 * Implementations of this interface are responsible for CRUD (Create, Read, Update, Delete) operations on entities.
 *
 * <p>
 *     The repository is designed to provide a common set of methods for interacting with the underlying storage
 *     mechanism, whether it be a relational database, a NoSQL database, or any other data store.
 * </p>
 *
 * <p>
 *     Implementing classes should provide concrete implementations for the methods declared in this interface.
 * </p>
 */
public interface PhoneRepository {

  /**
   * Saves a given phone. Use the returned instance for further operations as the save operation might have changed the
   * phone instance completely.
   *
   * @param phone must not be {@literal null}.
   */
  void saveOrUpdate(Phone phone);

  void saveAllPhones(List<Phone> phones);

  /**
   * Returns all phones stored in the repository.
   *
   * @return all phones
   */
  List<Phone> findAll();

  /**
   * Clear all entities stored in the repository.
   */
  void clear();

  /**
   * Finds a {@link Phone} by its name.
   *
   * @param name the name of the phone
   * @return the phone with the given name or {@literal Optional#empty()} if none found.
   */
  Optional<Phone> findByName(String name);
}
