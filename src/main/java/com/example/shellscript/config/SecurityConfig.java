package com.example.shellscript.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user").password("{noop}user").roles("USER").build());
        return manager;
    }

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl("ldaps://ldap-proxy.cse.iith.ac.in:636");
        ldapContextSource.setUserDn("cn=admin,dc=cse,dc=iith,dc=ac,dc=in");
        // ldapContextSource.setPassword("cseldap1234");
        return ldapContextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       BaseLdapPathContextSource contextSource) {
        // In-Memory auth provider
        DaoAuthenticationProvider inMemoryProvider = new DaoAuthenticationProvider();
        inMemoryProvider.setUserDetailsService(userDetailsService);

        // LDAP auth provider
        LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(contextSource);
        factory.setUserDnPatterns("cn={0},ou=people,dc=cse,dc=iith,dc=ac,dc=in");
        AuthenticationManager ldapAuthManager = factory.createAuthenticationManager();

        return new ProviderManager(Arrays.asList(
                inMemoryProvider,
                ((ProviderManager) ldapAuthManager).getProviders().get(0)
        ));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/custo", "/runshellscript**", "/layout", "/create", "/usercount", "/update", "/delete", "/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/custo")
                        .loginProcessingUrl("/login")     // Form submits here
                        .defaultSuccessUrl("/layout", true)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }
}
