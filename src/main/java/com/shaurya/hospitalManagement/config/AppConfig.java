package com.shaurya.hospitalManagement.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Commenting this Bean will result in not making any bean
    // as we are adding our own method for authentication using JWT and OAuth
    // Also we wish to separate App and WebSecurity configs
    // This part is also in WebSecurity.java
    //@Bean
    //UserDetailsService userDetailsService() {
    //    UserDetails user1 = User.withUsername("admin")
    //            .password(passwordEncoder().encode("pass"))
    //            .roles("ADMIN")
    //            .build();
    //    UserDetails user2 = User.withUsername("patient")
    //            .password(passwordEncoder().encode("pass"))
    //            .roles("PATIENT")
    //            .build();
    //    return new InMemoryUserDetailsManager(user1, user2);
    //}
}
