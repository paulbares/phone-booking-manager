package com.phone.manager.app.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Basic security configuration.
 */
@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf().disable()
            .authorizeHttpRequests()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic();

    return httpSecurity.build();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    UserDetails peter = User.withDefaultPasswordEncoder()
            .username("peter")
            .password("1234")
            .build();
    UserDetails paul = User.withDefaultPasswordEncoder()
            .username("paul")
            .password("1234")
            .build();
    return new InMemoryUserDetailsManager(peter, paul);
  }
}