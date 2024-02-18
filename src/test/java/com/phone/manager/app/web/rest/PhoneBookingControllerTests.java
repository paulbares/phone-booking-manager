package com.phone.manager.app.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.phone.manager.app.Constants;
import com.phone.manager.app.TestUtil;
import com.phone.manager.app.domain.Phone;
import com.phone.manager.app.service.Availability;
import com.phone.manager.app.service.MapPhoneBookingService;
import com.phone.manager.app.service.PhoneBookingService;
import com.phone.manager.app.service.dto.PhoneRequestDto;
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

import java.time.Instant;
import java.util.List;

import static com.phone.manager.app.Constants.PHONE_NAMES;
import static com.phone.manager.app.service.Availability.YES;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link PhoneBookingController} REST controller.
 */
@SpringBootTest(classes = {
        PhoneBookingControllerTests.PhoneBookingControllerTestConfiguration.class
})
@AutoConfigureMockMvc(/* Disable spring security */ addFilters = false)
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
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn()
            .getResponse();

    List<PhoneDto> phones = TestUtil.MAPPER.readValue(response.getContentAsString(), new TypeReference<>() {
    });
    Assertions
            .assertThat(phones)
            .containsExactlyInAnyOrderElementsOf(Constants.PHONE_NAMES.stream().map(n -> new PhoneDto(n, YES, null, null)).toList());
  }

  @Test
  void testBookAndReturn() throws Exception {
    this.mockMvc
            .perform(
                    post("/api/book")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(TestUtil.MAPPER.writeValueAsString(new PhoneRequestDto(Constants.PHONE_NAMES.get(0), "peter")))
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
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(TestUtil.MAPPER.writeValueAsString(new PhoneRequestDto(Constants.PHONE_NAMES.get(0), "peter")))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    all = this.service.getAllPhones();
    Assertions.assertThat(all.stream().filter(p -> p.getAvailability() == Availability.YES).count()).isEqualTo(PHONE_NAMES.size());
  }
}
