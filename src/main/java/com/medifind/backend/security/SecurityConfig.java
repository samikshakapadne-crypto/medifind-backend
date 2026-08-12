package com.medifind.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/pharmacies/**"
                        )
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/medicines/**"
                        )
                        .permitAll()

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/medicines"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/medicines/**"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/medicines/**"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/inventory/search"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/inventory/pharmacy/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/inventory"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/inventory/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/rare-medicine"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/rare-medicine/my-requests"
                        ).authenticated()

                        .requestMatchers("/api/rare-medicine/pending")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                "/api/rare-medicine/*/approve",
                                "/api/rare-medicine/*/reject"
                        ).hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}