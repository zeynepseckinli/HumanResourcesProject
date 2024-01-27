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
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain) throws ServletException, IOException {
//        /**
//         * Uygulamanız içine gelen herbir istek bearer dan geçmek zorundadır. Bizde burada gelen isteklerin
//         * kontrolünü yaparak yetkili bir tokenlarının olup olmadığına bakarız. Böylece oturum kontrolü
//         * sağlamış oluruz.
//         * Burada ilk olarak gelen isteğin Header'ı içinde Bearer Token bilgisi var mı kontrol ederiz.
//         * Sonra bu token'ı ayrıştırarak geçerliliğini kontrol ederiz.
//         * Geçerli bir token ise bu token'a sahip kullanıcının yetkilerini kontrol ederiz.
//         * Tüm bu işlemlerden sonra Spring için yetki kontrolünde kullanılabileceği bir UserDetails
//         * nesnesi oluşturarak Filter'ın arasına yerleştiririz.
//         */
//
//        String bearer_Token = request.getHeader("Authorization");//gelen isteğin header'ındaki Authorization bilgisini çek
//        if (Objects.nonNull(bearer_Token) && bearer_Token.startsWith("Bearer ")) { //bearer token'ı kontrol et
//            String token = bearer_Token.substring(7);//token'ı oku
//            Optional<Long> authId = jwtTokenManager.getIdByToken(token);//token içinden kullanıcı id çek
//            if (authId.isEmpty()) {
//                throw new UserException(ErrorType.INVALID_TOKEN);
//            }
//            /**
//             * Spring Security'nin yönetebileceği bir auth nesnesi tanımlıyoruz.
//             */
//            UserDetails userDetails = jwtUserDetails.findByAuthId(authId.get());
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            /**
//             * Auth nesnesini güvenlik kabının içine entegre ediyoruz. Böylece oturum oluşturacak ve bunun
//             * yetkileri üzerinden sayfalara erişim açacaktır.
//             */
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        }
//        filterChain.doFilter(request, response);
//    }
//}
