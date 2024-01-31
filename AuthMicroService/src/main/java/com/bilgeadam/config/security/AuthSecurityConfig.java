//package com.bilgeadam.config.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//
//@Configuration
//public class AuthSecurityConfig {
//
//    @Bean
//    JwtTokenFilter getJwtTokenFilter() {
//        return new JwtTokenFilter();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//
//        httpSecurity.authorizeHttpRequests(req ->
//                req.requestMatchers(
//                                "/swagger-ui/**",//swagger arayüzü
//                                "/v3/api-docs/**",//swagger end-pointler-json
//                                "/api/v1/auth/get-message",
//                                "/api/v1/auth/save",
//                                "/api/v1/auth/login",
//                                "/api/v1/auth/update",
//                                "/api/v1/auth/update",
//                                "/api/v1/auth/update-state",
//                                "/api/v1/auth/change-password"
//                        ).permitAll()
//                        .requestMatchers(  "/api/v1/auth/auth-message").hasAnyRole("ADMIN")
//                        .anyRequest()
//                        .authenticated()
//        );
//        httpSecurity.csrf(AbstractHttpConfigurer::disable);
//
//        httpSecurity.addFilterBefore(getJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//        return httpSecurity.build();
//    }
//}
