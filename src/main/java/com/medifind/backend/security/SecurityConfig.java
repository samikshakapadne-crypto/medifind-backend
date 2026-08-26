package com.medifind.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
                // ==========================================
                // BASIC SECURITY
                // ==========================================

                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // ==========================================
                // AUTHORIZATION
                // ==========================================

                .authorizeHttpRequests(auth -> auth


                        // ==========================================
                        // CORS PREFLIGHT
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()


                        // ==========================================
                        // AUTHENTICATION - PUBLIC
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register"
                        )
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login"
                        )
                        .permitAll()


                        // Only existing ADMIN can create another admin
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register-admin"
                        )
                        .hasRole("ADMIN")


                        // ==========================================
                        // PHARMACY REGISTRATION - PUBLIC
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/pharmacies"
                        )
                        .permitAll()


                        // ==========================================
                        // PUBLIC PHARMACY VIEW
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/pharmacies/**"
                        )
                        .permitAll()


                        // ==========================================
                        // PUBLIC MEDICINE CATALOG
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/medicines/**"
                        )
                        .permitAll()


                        // ==========================================
                        // MEDICINE MANAGEMENT - ADMIN
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/medicines"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/medicines/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/medicines/**"
                        )
                        .hasRole("ADMIN")


                        // ==========================================
                        // PUBLIC STOCK SEARCH
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/inventory/search"
                        )
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/inventory/customer-search"
                        )
                        .permitAll()


                        // ==========================================
                        // PHARMACY INVENTORY
                        // ==========================================

                        // Pharmacy and Admin can view inventory
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/inventory/pharmacy/**"
                        )
                        .hasAnyRole(
                                "PHARMACY",
                                "ADMIN"
                        )

                        // Only Pharmacy can add inventory
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/inventory"
                        )
                        .hasRole("PHARMACY")

                        // Only Pharmacy can update inventory
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/inventory/**"
                        )
                        .hasRole("PHARMACY")


                        // ==========================================
                        // CUSTOMER CART
                        // ==========================================

                        .requestMatchers(
                                "/api/cart/**"
                        )
                        .hasRole("CUSTOMER")


                        // ==========================================
                        // CUSTOMER ORDERS
                        // ==========================================

                        .requestMatchers(
                                "/api/orders/**"
                        )
                        .hasRole("CUSTOMER")


                        // ==========================================
                        // PRESCRIPTIONS - CUSTOMER
                        // ==========================================

                        // Customer uploads prescription
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/prescriptions"
                        )
                        .hasRole("CUSTOMER")

                        // Customer views own prescriptions
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/prescriptions/my"
                        )
                        .hasRole("CUSTOMER")


                        // ==========================================
                        // PRESCRIPTIONS - PHARMACY
                        // ==========================================

                        // Includes:
                        //
                        // GET
                        // /api/pharmacy/prescriptions/pending
                        //
                        // PUT
                        // /api/pharmacy/prescriptions/{id}/approve
                        //
                        // PUT
                        // /api/pharmacy/prescriptions/{id}/reject

                        .requestMatchers(
                                "/api/pharmacy/prescriptions/**"
                        )
                        .hasRole("PHARMACY")


                        // ==========================================
                        // ADMIN APIs
                        // ==========================================

                        // Examples:
                        //
                        // /api/admin/customers
                        // /api/admin/pharmacies
                        // /api/admin/orders
                        // /api/admin/orders/{id}

                        .requestMatchers(
                                "/api/admin/**"
                        )
                        .hasRole("ADMIN")


                        // ==========================================
                        // RARE MEDICINE - CUSTOMER
                        // ==========================================

                        // Customer creates rare medicine request
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/rare-medicine"
                        )
                        .hasRole("CUSTOMER")

                        // IMPORTANT:
                        // This must come before the wider
                        // /api/rare-medicine/* GET rule
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/rare-medicine/my-requests"
                        )
                        .hasRole("CUSTOMER")


                        // ==========================================
                        // RARE MEDICINE - ADMIN
                        // ==========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/rare-medicine/pending"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/rare-medicine/*/approve"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/rare-medicine/*/reject"
                        )
                        .hasRole("ADMIN")

                        // Admin views one rare medicine request
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/rare-medicine/*"
                        )
                        .hasRole("ADMIN")


                        // ==========================================
                        // EVERYTHING ELSE
                        // ==========================================

                        .anyRequest()
                        .authenticated()
                )


                // ==========================================
                // DISABLE DEFAULT LOGIN METHODS
                // ==========================================

                .formLogin(form ->
                        form.disable()
                )

                .httpBasic(basic ->
                        basic.disable()
                )


                // ==========================================
                // JWT FILTER
                // ==========================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


    // ==========================================================
    // CORS CONFIGURATION
    // ==========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // Allows frontend from localhost,
        // Netlify, Vercel, etc.
        configuration.setAllowedOriginPatterns(
                List.of("*")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        // JWT is sent through Authorization header.
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}