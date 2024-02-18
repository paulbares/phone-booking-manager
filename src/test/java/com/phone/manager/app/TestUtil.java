package com.phone.manager.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.phone.manager.app.exception.PhoneNotAvailableException;
import org.assertj.core.api.Assertions;
import org.hibernate.StaleObjectStateException;

/**
 * Utility class for testing REST controllers.
 */
public final class TestUtil {

  public static final ObjectMapper MAPPER = createObjectMapper();

  private TestUtil() {
  }

  private static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }

  public static void assertThrowable(Throwable t) {
    if (t instanceof StaleObjectStateException) {
      Assertions.assertThat(t).hasMessageContaining("Row was updated or deleted by another transaction");
    } else if (t instanceof PhoneNotAvailableException) {
      Assertions.assertThat(t).hasMessageContaining("not available");
    } else {
      Assertions.fail(t + " was expected to be of type " + StaleObjectStateException.class
              + " or " + PhoneNotAvailableException.class
              + " but was " + t.getClass());
    }
  }

  /**
   * Gets the root cause of an error.
   * <p>
   * This particularly consider the special case where an error cause is itself. It avoids infinite loops.
   *
   * @param error error to consider
   * @return the root cause of the error
   */
  public static Throwable getRootCause(Throwable error) {
    Throwable cause;
    while ((cause = getCause(error)) != null) {
      error = cause;
    }

    return error;
  }

  /**
   * Gets the cause of an error.
   * <p>
   * This particularly consider the special case where an error cause is itself. It avoids infinite loops.
   *
   * @param error error to consider
   * @return the cause of the error or {@code null} if none
   */
  public static Throwable getCause(final Throwable error) {
    final Throwable cause = error.getCause();
    return cause == error ? null : cause;
  }
}
