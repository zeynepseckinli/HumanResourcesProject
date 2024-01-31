//package com.bilgeadam.config.security;
//
//import com.bilgeadam.exception.ErrorType;
//import com.bilgeadam.exception.UserException;
//import com.bilgeadam.utility.JwtTokenManager;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
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
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain) throws ServletException, IOException {
//        /**
//         * Uygulamamız içine gelen herbir istek bearer dan geçmek zorundadır. Bizde burada gelen isteklerin
//         * kontrolünü yaparak yetkili bir tokenlarının olup olmadığına bakarız. Böylece oturum kontrolü
//         * sağlamış oluruz.
//         * Burada ilk olarak gelen isteğin Header'ı içinde Bearer Token bilgisi var mı kontrol ederiz.
//         * Sonra bu token'ı ayrıştırarak geçerliliğini kontrol ederiz.
//         * Geçerli bir token ise bu token'a sahip kullanıcının yetkilerini kontrol ederiz.
//         * Tüm bu işlemlerden sonra Spring için yetki kontrolünde kullanılabileceği bir UserDetails
//         * nesnesi oluşturarak Filter'ın arasına yerleştiririz.->UserSecurityConfig sınıfında
//         */
//
//        final String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            Optional<String> userRole = jwtTokenManager.getRoleFromToken(token);//jwtToken'dan gelen token
//            if (jwtTokenManager.validateToken(token)) {
//           UserDet
//                UsernamePasswordAuthenticationToken authenticationToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);//spring için oluşturduğumuz token
//            } else {
//                throw new UserException(ErrorType.INVALID_TOKEN);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//}
