//package com.bilgeadam.config.security;
//
//
//import com.bilgeadam.exception.AuthException;
//import com.bilgeadam.exception.ErrorType;
//import com.bilgeadam.utility.JwtTokenManager;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Objects;
//import java.util.Optional;
//
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtTokenManager jwtTokenManager;
//
//    @Autowired
//    private JwtUserDetails jwtUserDetails;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//
//        String bearer_token = request.getHeader("Authorization");
//       // System.out.println(bearer_token);
//
//        if (Objects.nonNull(bearer_token) && bearer_token.startsWith("Bearer ")) {
//            String token = bearer_token.substring(7);//token okunur
//            Optional<Long> id = jwtTokenManager.getIdByToken(token);//token içinden authId çekilir
//            if (id.isEmpty()) {
//                throw new AuthException(ErrorType.INVALID_TOKEN);
//            }
//            /**
//             * Spring Security'nin yönetebileceği bir auth nesnesi tanımlıyoruz
//             */
//            UserDetails userDetails = jwtUserDetails.findByAuthId(id.get());
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            /**
//             * Auth nesnesini güvenlik kabının içine entegre ediyoruz. Böylece oturum oluşturacak ve bunun
//             * yetkileri üzerinden sayfalara erişim açılacaktır.
//             */
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//
//        }
//
//        filterChain.doFilter(request, response);
//
//    }
//}
