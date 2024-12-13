package com.example.shellscript.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user").password("{noop}").roles("USER").build());
        return manager;
    }


    // Configure Basic Authentication
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers("/actuator/**","/images/**", "/css/**", "/js/**", "/static/**",
                        "/login", "/custo", "/runshellscript","/layout","/create").permitAll()  // Allow unauthenticated access to login and index pages
                .anyRequest().authenticated()  // Secure all other requests
                .and()
                .formLogin(form -> form
                       .loginPage("/custo")  // Custom login page URL
                       //.loginProcessingUrl("/login")  // URL for form submission
                        .defaultSuccessUrl("/layout", true)  // Redirect to /index after successful login
                        .permitAll()  // Allow all to access the login page
                )
                .logout(logout -> logout
                        .permitAll()  // Allow all to access logout functionality
                );// Disable CSRF for simplicity in this example

        return http.build();
    }
}
