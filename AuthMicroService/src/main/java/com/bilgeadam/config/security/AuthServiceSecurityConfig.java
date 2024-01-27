package com.bilgeadam.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthServiceSecurityConfig {

    @Bean
    JwtTokenFilter getJwtTokenFilter() {
        return new JwtTokenFilter();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable();//cross-site-request-forgery
        /**
         * aşağıdaki kodumuz:
         * httpSecurity.authorizeRequests() -> request'lere izin temasını açtım
         *  .requestMatchers("/v3/api-docs/**", "/swagger-ui/**") -> eşleşecek izinleri belirttim
         *   .permitAll() -> hepsine izin ver
         *   .anyRequest().authenticated(); -> bunun dışında kalan bütün istekler authentication'a tabii tut
         */

//        httpSecurity.authorizeHttpRequests(req ->
//                req.requestMatchers(
        httpSecurity.authorizeRequests().requestMatchers(//TODO: Auth'un erişimine izin verilecek end-pointleri düzenle
                        "/swagger-ui/**",//swagger arayüzü getirecek
                        "/v3/api-docs/**",//swagger arayüzdeki end-pointleri getirecek
                        "/api/v1/auth/getmessage",
                        "/api/v1/auth/save",
                        "/api/v1/auth/login"
                ).permitAll()
                .anyRequest()
                .authenticated();

        //  httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.addFilterBefore(getJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
