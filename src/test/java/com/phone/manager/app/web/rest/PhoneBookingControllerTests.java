package com.phone.manager.app.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.phone.manager.app.Constants;
import com.phone.manager.app.TestUtil;
import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.service.Availability;
import com.phone.manager.app.service.MapPhoneBookingService;
import com.phone.manager.app.service.PhoneBookingService;
import com.phone.manager.app.spring.config.SecurityConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.Instant;
import java.util.List;

import static com.phone.manager.app.Constants.PHONE_NAMES;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link PhoneBookingController} REST controller.
 */
@SpringBootTest(classes = {
        PhoneBookingController.class,
        PhoneBookingControllerTests.PhoneBookingControllerTestConfiguration.class,
        SecurityConfig.class,
        AppErrorHandler.class
})
@AutoConfigureMockMvc
@EnableWebMvc
class PhoneBookingControllerTests {

  @TestConfiguration
  static class PhoneBookingControllerTestConfiguration {

    @Bean
    @Primary
    public PhoneBookingService mapPhoneBookingService() {
      MapPhoneBookingService service = new MapPhoneBookingService(Instant::now);
      service.addPhones(Constants.PHONE_NAMES);
      return service;
    }
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PhoneBookingService service;

  @Test
  void testGetAllPhones() throws Exception {
    MockHttpServletResponse response = this.mockMvc
            .perform(
                    get("/api/phones")
                            .accept(MediaType.APPLICATION_JSON)
                            .with(httpBasic("peter", "1234"))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn()
            .getResponse();

    List<PhoneDto> phones = TestUtil.MAPPER.readValue(response.getContentAsString(), new TypeReference<>() {
    });
    Assertions
            .assertThat(phones.stream().map(PhoneDto::getName).toList())
            .containsExactlyInAnyOrderElementsOf(Constants.PHONE_NAMES);
  }

  @Test
  void testBookAndReturn() throws Exception {
    this.mockMvc
            .perform(
                    post("/api/book")
                            .accept(MediaType.APPLICATION_JSON)
                            .content(Constants.PHONE_NAMES.get(0))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(httpBasic("peter", "1234"))

            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    List<Phone> all = this.service.getAllPhones();
    Assertions.assertThat(all.stream().filter(p -> p.getAvailability() == Availability.YES).count()).isEqualTo(PHONE_NAMES.size() - 1);
    Phone petersPhone = this.service.getPhone(PHONE_NAMES.get(0));
    Assertions.assertThat(petersPhone.getAvailability()).isEqualTo(Availability.NO);
    Assertions.assertThat(petersPhone.getBorrower()).isEqualTo("peter");

    this.mockMvc
            .perform(
                    post("/api/return")
                            .with(httpBasic("peter", "1234"))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(Constants.PHONE_NAMES.get(0))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    all = this.service.getAllPhones();
    Assertions.assertThat(all.stream().filter(p -> p.getAvailability() == Availability.YES).count()).isEqualTo(PHONE_NAMES.size());
  }

  @Test
  void testBookUnknownDevice() throws Exception {
    // HTTP STATUS CODE = 404 => Not found
    this.mockMvc
            .perform(
                    post("/api/book")
                            .accept(MediaType.APPLICATION_JSON)
                            .content("unknown name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(httpBasic("peter", "1234"))

            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
  }

  @Test
  void testReturnPhoneByIncorrectBorrower() throws Exception {
    this.mockMvc
            .perform(
                    post("/api/book")
                            .accept(MediaType.APPLICATION_JSON)
                            .content(Constants.PHONE_NAMES.get(0))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(httpBasic("peter", "1234"))

            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    // HTTP STATUS CODE = 400 => Bad request
    this.mockMvc
            .perform(
                    post("/api/return")
                            .with(httpBasic("paul", "1234"))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(Constants.PHONE_NAMES.get(0))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    // Correct borrower
    this.mockMvc
            .perform(
                    post("/api/return")
                            .with(httpBasic("peter", "1234"))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(Constants.PHONE_NAMES.get(0))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
  }

  @Test
  void testBookPhoneNotAvailable() throws Exception {
    // Peter book a phone
    this.mockMvc
            .perform(
                    post("/api/book")
                            .accept(MediaType.APPLICATION_JSON)
                            .content(Constants.PHONE_NAMES.get(0))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(httpBasic("peter", "1234"))

            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    // Paul tries to book the same phone
    this.mockMvc
            .perform(
                    post("/api/book")
                            .accept(MediaType.APPLICATION_JSON)
                            .content(Constants.PHONE_NAMES.get(0))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(httpBasic("paul", "1234"))

            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    // Peter return the phone
    this.mockMvc
            .perform(
                    post("/api/return")
                            .with(httpBasic("peter", "1234"))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(Constants.PHONE_NAMES.get(0))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
  }
}
