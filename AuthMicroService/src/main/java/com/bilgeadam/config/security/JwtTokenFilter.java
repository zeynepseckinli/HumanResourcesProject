package com.bilgeadam.config.security;

import com.bilgeadam.exception.AuthException;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.utility.JwtTokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;


public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Autowired
    private JwtUserDetails jwtUserDetails;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Optional<Long> id = jwtTokenManager.getIdByToken(token);
            if (id.isPresent()) {
                UserDetails userDetails = jwtUserDetails.loadUserByUserId(id.get());
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                throw new AuthException(ErrorType.INVALID_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }
//    @Autowired
//    private JwtUserDetails jwtUserDetails;


//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String bearer_Token = request.getHeader("Authorization");
//
//        if (Objects.nonNull(bearer_Token) && bearer_Token.startsWith("Bearer ")) {
//            //Token okunur
//            String token = bearer_Token.substring(7);
//            // Token içinden kullanıcı id'si çekilir.
//            Optional<Long> authId = jwtTokenManager.getIdByToken(token);
//            //Eğer token içinden geçerli bir id dönmez ise hata fırlatıyoruz.
//            if (authId.isEmpty()) {
//                throw new RuntimeException("Geçersiz Token");
//            }
//            /**
//             * Spring Security'nin yönetebileceği bir auth nesnesi tanımlıyoruz.
//             */
//            UserDetails userDetails = jwtUserDetails.loadUserByUserId(authId.get());
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            /**
//             * Auth nesnesini güvenlik kabının içine entegre ediyoruz. Böylece oturum oluşturacak ve bunun
//             * yetkileri üzerinden sayfalara erişim açacaktır.
//             */
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        }
//
//        filterChain.doFilter(request, response);
//
//    }
}
